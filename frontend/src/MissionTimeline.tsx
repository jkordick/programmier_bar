import { useState, useEffect, useCallback } from 'react';
import type { Mission, MissionStatus } from './types';
import { MISSION_STATUS_ICONS, MISSION_STATUS_LABELS } from './types';
import { fetchMissions, createMission, deleteMission } from './api';

interface MissionTimelineProps {
  devId: number;
  callSign: string;
  onClose: () => void;
}

const EMPTY_MISSION: Mission = {
  title: '',
  description: '',
  date: new Date().toISOString().slice(0, 10),
  difficultyRating: 3,
  status: 'IN_PROGRESS',
};

export default function MissionTimeline({ devId, callSign, onClose }: MissionTimelineProps) {
  const [missions, setMissions] = useState<Mission[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState<Mission>({ ...EMPTY_MISSION });

  const loadMissions = useCallback(async () => {
    setLoading(true);
    try {
      const data = await fetchMissions(devId);
      setMissions(data);
    } catch {
      console.error('Failed to load missions');
    } finally {
      setLoading(false);
    }
  }, [devId]);

  useEffect(() => {
    loadMissions();
  }, [loadMissions]);

  async function handleAddMission(e: React.FormEvent) {
    e.preventDefault();
    try {
      await createMission(devId, form);
      setForm({ ...EMPTY_MISSION });
      setShowForm(false);
      loadMissions();
    } catch {
      console.error('Failed to add mission');
    }
  }

  async function handleDeleteMission(missionId: number) {
    try {
      await deleteMission(devId, missionId);
      loadMissions();
    } catch {
      console.error('Failed to delete mission');
    }
  }

  function renderDifficultyStars(rating: number) {
    return '⭐'.repeat(rating) + '☆'.repeat(5 - rating);
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal mission-modal" onClick={e => e.stopPropagation()}>
        <h2>📜 Mission Log — {callSign}</h2>

        <button
          className="btn btn-primary"
          style={{ marginBottom: '1rem', width: '100%' }}
          onClick={() => setShowForm(!showForm)}
        >
          {showForm ? '✕ Cancel' : '+ Log New Mission'}
        </button>

        {showForm && (
          <form className="mission-form" onSubmit={handleAddMission}>
            <div className="form-group">
              <label>Mission Title *</label>
              <input
                required
                value={form.title}
                onChange={e => setForm({ ...form, title: e.target.value })}
                placeholder="Deploy Nebula ORM v2.0"
              />
            </div>
            <div className="form-group">
              <label>Description</label>
              <textarea
                value={form.description}
                onChange={e => setForm({ ...form, description: e.target.value })}
                placeholder="Brief mission debrief..."
              />
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Date *</label>
                <input
                  type="date"
                  required
                  value={form.date}
                  onChange={e => setForm({ ...form, date: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label>Difficulty (1-5)</label>
                <input
                  type="number"
                  min={1}
                  max={5}
                  value={form.difficultyRating}
                  onChange={e => setForm({ ...form, difficultyRating: +e.target.value })}
                />
              </div>
            </div>
            <div className="form-group">
              <label>Status</label>
              <select
                value={form.status}
                onChange={e => setForm({ ...form, status: e.target.value as MissionStatus })}
              >
                {Object.entries(MISSION_STATUS_LABELS).map(([key, label]) => (
                  <option key={key} value={key}>{label}</option>
                ))}
              </select>
            </div>
            <div className="form-actions">
              <button type="button" className="btn" onClick={() => setShowForm(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary">Launch Mission</button>
            </div>
          </form>
        )}

        {loading ? (
          <div className="loading" style={{ padding: '2rem' }}>⟐ Loading missions...</div>
        ) : missions.length === 0 ? (
          <div className="empty-state" style={{ padding: '2rem' }}>
            <div className="empty-icon">🌑</div>
            <p>No missions logged yet.</p>
            <p>Add one to start the mission timeline!</p>
          </div>
        ) : (
          <div className="mission-timeline">
            {missions.map(mission => (
              <div key={mission.id} className={`mission-entry mission-${mission.status.toLowerCase().replace(/_/g, '-')}`}>
                <div className="mission-header">
                  <span className="mission-status-icon">{MISSION_STATUS_ICONS[mission.status]}</span>
                  <div className="mission-info">
                    <div className="mission-title">{mission.title}</div>
                    <div className="mission-date">{new Date(mission.date).toLocaleDateString()}</div>
                  </div>
                  <button
                    className="btn btn-danger mission-delete-btn"
                    onClick={() => mission.id && handleDeleteMission(mission.id)}
                  >
                    ✕
                  </button>
                </div>
                {mission.description && (
                  <div className="mission-description">{mission.description}</div>
                )}
                <div className="mission-meta">
                  <span className="mission-difficulty">{renderDifficultyStars(mission.difficultyRating)}</span>
                  <span className="mission-status-label">{MISSION_STATUS_LABELS[mission.status]}</span>
                </div>
              </div>
            ))}
          </div>
        )}

        <div className="form-actions" style={{ marginTop: '1rem' }}>
          <button className="btn" onClick={onClose}>Close</button>
        </div>
      </div>
    </div>
  );
}
