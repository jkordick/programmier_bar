import { useState, useEffect, useCallback } from 'react';
import type { SpaceDeveloper, Crew } from './types';
import { fetchDevs, createDev, updateDev, deleteDev, fetchRandomJoke, fetchCrews, createCrew, deleteCrew, joinCrew, leaveCrew } from './api';
import DevCard from './DevCard';
import DevForm from './DevForm';
import MissionTimeline from './MissionTimeline';
import CrewList from './CrewList';
import CrewDetail from './CrewDetail';
import CrewForm from './CrewForm';

export default function App() {
  const [devs, setDevs] = useState<SpaceDeveloper[]>([]);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState<SpaceDeveloper | null>(null);
  const [creating, setCreating] = useState(false);
  const [missionDev, setMissionDev] = useState<SpaceDeveloper | null>(null);
  const [joke, setJoke] = useState('Click to receive a transmission from the joke nebula...');

  const [crews, setCrews] = useState<Crew[]>([]);
  const [selectedCrew, setSelectedCrew] = useState<Crew | null>(null);
  const [creatingCrew, setCreatingCrew] = useState(false);

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

  const loadCrews = useCallback(async () => {
    try {
      const data = await fetchCrews();
      setCrews(data);
    } catch {
      console.error('Failed to load crews');
    }
  }, []);

  useEffect(() => {
    loadDevs();
    loadCrews();
  }, [loadDevs, loadCrews]);

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
    loadCrews();
  }

  async function handleJoke() {
    try {
      const j = await fetchRandomJoke();
      setJoke(j);
    } catch {
      setJoke('The joke singularity collapsed. Try again.');
    }
  }

  async function handleCreateCrew(crew: { name: string; missionStatement: string; shipName: string }) {
    await createCrew(crew);
    setCreatingCrew(false);
    loadCrews();
  }

  async function handleDeleteCrew(id: number) {
    await deleteCrew(id);
    setSelectedCrew(null);
    loadCrews();
    loadDevs();
  }

  async function handleJoinCrew(crewId: number, devId: number) {
    try {
      const updated = await joinCrew(crewId, devId);
      setSelectedCrew(updated);
      loadCrews();
      loadDevs();
    } catch (err) {
      console.error('Failed to join crew:', err);
    }
  }

  async function handleLeaveCrew(crewId: number, devId: number) {
    try {
      const updated = await leaveCrew(crewId, devId);
      setSelectedCrew(updated);
      loadCrews();
      loadDevs();
    } catch (err) {
      console.error('Failed to leave crew:', err);
    }
  }

  return (
    <>
      <header className="header">
        <h1>SPACE DEVS</h1>
        <p>Intergalactic Developer Registry</p>
      </header>

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

      <CrewList
        crews={crews}
        onSelect={setSelectedCrew}
        onCreate={() => setCreatingCrew(true)}
      />

      {creatingCrew && (
        <CrewForm onSave={handleCreateCrew} onCancel={() => setCreatingCrew(false)} />
      )}
      {selectedCrew && (
        <CrewDetail
          crew={selectedCrew}
          allDevs={devs}
          onJoin={handleJoinCrew}
          onLeave={handleLeaveCrew}
          onDelete={handleDeleteCrew}
          onClose={() => setSelectedCrew(null)}
        />
      )}
    </>
  );
}
