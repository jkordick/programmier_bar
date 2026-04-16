import { useState, useEffect, useCallback } from 'react';
import type { SpaceDeveloper } from './types';
import { fetchDevs, createDev, updateDev, deleteDev, fetchRandomJoke } from './api';
import DevCard from './DevCard';
import DevForm from './DevForm';
import MissionTimeline from './MissionTimeline';
import Leaderboard from './Leaderboard';

type Tab = 'registry' | 'leaderboard';

export default function App() {
  const [activeTab, setActiveTab] = useState<Tab>('registry');
  const [devs, setDevs] = useState<SpaceDeveloper[]>([]);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState<SpaceDeveloper | null>(null);
  const [creating, setCreating] = useState(false);
  const [missionDev, setMissionDev] = useState<SpaceDeveloper | null>(null);
  const [joke, setJoke] = useState('Click to receive a transmission from the joke nebula...');

  const loadDevs = useCallback(async () => {
    setLoading(true);
    try {
      const data = await fetchDevs();
      setDevs(data);
    } catch {
      console.error('Failed to load space devs');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadDevs();
  }, [loadDevs]);

  async function handleCreate(dev: SpaceDeveloper) {
    await createDev(dev);
    setCreating(false);
    loadDevs();
  }

  async function handleUpdate(dev: SpaceDeveloper) {
    if (editing?.id) {
      await updateDev(editing.id, dev);
      setEditing(null);
      loadDevs();
    }
  }

  async function handleDelete(id: number) {
    await deleteDev(id);
    loadDevs();
  }

  async function handleJoke() {
    try {
      const j = await fetchRandomJoke();
      setJoke(j);
    } catch {
      setJoke('The joke singularity collapsed. Try again.');
    }
  }

  return (
    <>
      <header className="header">
        <h1>SPACE DEVS</h1>
        <p>Intergalactic Developer Registry</p>
      </header>

      <nav className="tab-nav">
        <button
          className={`tab-btn ${activeTab === 'registry' ? 'tab-btn-active' : ''}`}
          onClick={() => setActiveTab('registry')}
        >
          🛰️ Registry
        </button>
        <button
          className={`tab-btn ${activeTab === 'leaderboard' ? 'tab-btn-active' : ''}`}
          onClick={() => setActiveTab('leaderboard')}
        >
          🏆 Leaderboard
        </button>
      </nav>

      <div style={{ display: activeTab === 'registry' ? 'block' : 'none' }}>
          <div className="toolbar">
            <button className="btn btn-primary" onClick={() => setCreating(true)}>
              + Register Space Dev
            </button>
            <button className="btn" onClick={loadDevs}>
              ↻ Scan Galaxy
            </button>
          </div>

          <div className="joke-banner">
            <div className="joke-label">📡 Incoming Transmission</div>
            <div className="joke-banner-text" onClick={handleJoke}>{joke}</div>
          </div>

          {loading ? (
            <div className="loading">⟐ Scanning the galaxy for developers...</div>
          ) : devs.length === 0 ? (
            <div className="empty-state">
              <div className="empty-icon">🌑</div>
              <p>No space devs found in this sector.</p>
              <p>Register one to populate the galaxy!</p>
            </div>
          ) : (
            <div className="dev-grid">
              {devs.map(dev => (
                <DevCard
                  key={dev.id}
                  dev={dev}
                  onEdit={setEditing}
                  onDelete={handleDelete}
                  onShowMissions={setMissionDev}
                />
              ))}
            </div>
          )}
      </div>

      <div style={{ display: activeTab === 'leaderboard' ? 'block' : 'none' }}>
        <Leaderboard />
      </div>

      {creating && (
        <DevForm onSave={handleCreate} onCancel={() => setCreating(false)} />
      )}
      {editing && (
        <DevForm dev={editing} onSave={handleUpdate} onCancel={() => setEditing(null)} />
      )}
      {missionDev && missionDev.id && (
        <MissionTimeline
          devId={missionDev.id}
          callSign={missionDev.callSign}
          onClose={() => setMissionDev(null)}
        />
      )}
    </>
  );
}
