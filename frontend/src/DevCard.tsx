import type { SpaceDeveloper } from './types';
import { SENIORITY_LABELS } from './types';

interface DevCardProps {
  dev: SpaceDeveloper;
  onEdit: (dev: SpaceDeveloper) => void;
  onDelete: (id: number) => void;
  onShowMissions: (dev: SpaceDeveloper) => void;
}

export default function DevCard({ dev, onEdit, onDelete, onShowMissions }: DevCardProps) {
  const powerPercent = Math.min((dev.debuggingPowerLevel / 9001) * 100, 100);

  return (
    <div className="dev-card">
      <div className="card-header">
        <div>
          <div className="call-sign">{dev.callSign}</div>
          <div className="real-name">{dev.realName}</div>
        </div>
        <span className="seniority-badge">{SENIORITY_LABELS[dev.seniority]}</span>
      </div>

      {dev.skills.length > 0 && (
        <div className="card-section">
          <div className="card-section-label">Skills</div>
          <div className="skills-list">
            {dev.skills.map((skill, i) => (
              <span key={i} className="skill-tag">{skill}</span>
            ))}
          </div>
        </div>
      )}

      {dev.ossProjects.length > 0 && (
        <div className="card-section">
          <div className="card-section-label">OSS Projects</div>
          <div className="skills-list">
            {dev.ossProjects.map((proj, i) => (
              <span key={i} className="skill-tag oss-tag">{proj}</span>
            ))}
          </div>
        </div>
      )}

      {dev.favoriteDevJoke && (
        <div className="card-section">
          <div className="card-section-label">Favorite Joke</div>
          <div className="joke-text">"{dev.favoriteDevJoke}"</div>
        </div>
      )}

      <div className="card-section">
        <div className="card-section-label">Debugging Power Level</div>
        <div className="power-bar-container">
          <div className="power-bar" style={{ width: `${powerPercent}%` }} />
        </div>
        <div style={{ fontSize: '0.7rem', color: 'var(--nebula-pink)', marginTop: '0.2rem' }}>
          {dev.debuggingPowerLevel} / 9001
          {dev.debuggingPowerLevel >= 9001 && ' — IT\'S OVER 9000!!!'}
        </div>
      </div>

      <div className="stats-grid">
        <div className="stat-item">
          <span className="stat-icon">☕</span>
          <span className="stat-value">{dev.coffeesPerDayInLiters}L</span> /day
        </div>
        <div className="stat-item">
          <span className="stat-icon">🔥</span>
          <span className="stat-value">{dev.gitCommitStreak}</span> streak
        </div>
        <div className="stat-item">
          <span className="stat-icon">🦆</span>
          {dev.rubberDuckName || 'None (brave)'}
        </div>
        <div className="stat-item">
          <span className="stat-icon">📊</span>
          <span className="stat-value">{dev.stackOverflowReputation.toLocaleString()}</span> rep
        </div>
        <div className="stat-item">
          <span className="stat-icon">⌨️</span>
          {dev.favoriteKeyboardShortcut || '???'}
        </div>
        <div className="stat-item">
          <span className="stat-icon">{dev.stillUsesVim ? '💪' : '🖱️'}</span>
          {dev.stillUsesVim ? 'Vim user' : 'GUI plebeian'}
        </div>
      </div>

      {dev.shipName && (
        <div className="ship-name">🚀 {dev.shipName}</div>
      )}

      <div className="card-actions">
        <button className="btn" onClick={() => onEdit(dev)}>Edit</button>
        <button className="btn" onClick={() => onShowMissions(dev)}>📜 Missions</button>
        <button className="btn btn-danger" onClick={() => dev.id && onDelete(dev.id)}>Deorbit</button>
      </div>
    </div>
  );
}
