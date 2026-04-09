import { useState, useEffect, useCallback } from 'react';
import type { SpaceDeveloper, LeaderboardSortField, SortOrder, Seniority } from './types';
import { SORT_FIELD_LABELS, SENIORITY_LABELS, ALL_SENIORITIES } from './types';
import { fetchLeaderboard } from './api';

const MEDAL_ICONS = ['🥇', '🥈', '🥉'];

export default function Leaderboard() {
  const [devs, setDevs] = useState<SpaceDeveloper[]>([]);
  const [loading, setLoading] = useState(true);
  const [sortBy, setSortBy] = useState<LeaderboardSortField>('debuggingPowerLevel');
  const [order, setOrder] = useState<SortOrder>('desc');
  const [selectedSeniorities, setSelectedSeniorities] = useState<Seniority[]>([]);
  const [skillFilter, setSkillFilter] = useState('');

  const loadLeaderboard = useCallback(async () => {
    setLoading(true);
    try {
      const data = await fetchLeaderboard(sortBy, order, selectedSeniorities, skillFilter);
      setDevs(data);
    } catch {
      console.error('Failed to load leaderboard');
    } finally {
      setLoading(false);
    }
  }, [sortBy, order, selectedSeniorities, skillFilter]);

  useEffect(() => {
    loadLeaderboard();
  }, [loadLeaderboard]);

  function toggleSeniority(s: Seniority) {
    setSelectedSeniorities(prev =>
      prev.includes(s) ? prev.filter(x => x !== s) : [...prev, s]
    );
  }

  function toggleOrder() {
    setOrder(prev => (prev === 'desc' ? 'asc' : 'desc'));
  }

  function getStatValue(dev: SpaceDeveloper): string {
    switch (sortBy) {
      case 'debuggingPowerLevel': return dev.debuggingPowerLevel.toLocaleString();
      case 'coffeesPerDayInLiters': return `${dev.coffeesPerDayInLiters}L`;
      case 'gitCommitStreak': return dev.gitCommitStreak.toLocaleString();
      case 'stackOverflowReputation': return dev.stackOverflowReputation.toLocaleString();
    }
  }

  return (
    <div className="leaderboard-container">
      <div className="leaderboard-filters">
        <div className="filter-section">
          <div className="filter-label">Rank By</div>
          <div className="sort-buttons">
            {(Object.keys(SORT_FIELD_LABELS) as LeaderboardSortField[]).map(field => (
              <button
                key={field}
                className={`btn btn-sort ${sortBy === field ? 'btn-sort-active' : ''}`}
                onClick={() => setSortBy(field)}
              >
                {SORT_FIELD_LABELS[field]}
              </button>
            ))}
          </div>
        </div>

        <div className="filter-section">
          <div className="filter-label">Order</div>
          <button className="btn btn-sort" onClick={toggleOrder}>
            {order === 'desc' ? '↓ Descending' : '↑ Ascending'}
          </button>
        </div>

        <div className="filter-section">
          <div className="filter-label">Seniority</div>
          <div className="seniority-filters">
            {ALL_SENIORITIES.map(s => (
              <label key={s} className="seniority-checkbox">
                <input
                  type="checkbox"
                  checked={selectedSeniorities.includes(s)}
                  onChange={() => toggleSeniority(s)}
                />
                <span>{SENIORITY_LABELS[s]}</span>
              </label>
            ))}
          </div>
        </div>

        <div className="filter-section">
          <div className="filter-label">Skill Search</div>
          <input
            type="text"
            className="skill-search-input"
            placeholder="e.g. Java, Rust, Python..."
            value={skillFilter}
            onChange={e => setSkillFilter(e.target.value)}
          />
        </div>
      </div>

      {loading ? (
        <div className="loading">⟐ Computing rankings...</div>
      ) : devs.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon">🌑</div>
          <p>No developers found</p>
          <p>Try adjusting your filters to discover more space devs.</p>
        </div>
      ) : (
        <div className="leaderboard-table">
          <div className="leaderboard-header-row">
            <span className="lb-col-rank">Rank</span>
            <span className="lb-col-callsign">Call Sign</span>
            <span className="lb-col-seniority">Seniority</span>
            <span className="lb-col-stat">{SORT_FIELD_LABELS[sortBy]}</span>
          </div>
          {devs.map((dev, idx) => (
            <div
              key={dev.id}
              className={`leaderboard-row ${idx < 3 ? `leaderboard-top-${idx + 1}` : ''}`}
            >
              <span className="lb-col-rank">
                {idx < 3 ? MEDAL_ICONS[idx] : `#${idx + 1}`}
              </span>
              <span className="lb-col-callsign">{dev.callSign}</span>
              <span className="lb-col-seniority">
                <span className="seniority-badge">{SENIORITY_LABELS[dev.seniority]}</span>
              </span>
              <span className="lb-col-stat lb-stat-value">{getStatValue(dev)}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
