import type { Crew } from './types';

interface CrewListProps {
  crews: Crew[];
  onSelect: (crew: Crew) => void;
  onCreate: () => void;
}

export default function CrewList({ crews, onSelect, onCreate }: CrewListProps) {
  return (
    <div className="crew-section">
      <div className="crew-section-header">
        <h2 className="crew-section-title">👥 Crews</h2>
        <button className="btn btn-primary" onClick={onCreate}>
          + Form New Crew
        </button>
      </div>

      {crews.length === 0 ? (
        <div className="empty-state" style={{ padding: '2rem' }}>
          <div className="empty-icon">🌑</div>
          <p>No crews formed yet.</p>
          <p>Create one to start collaborating!</p>
        </div>
      ) : (
        <div className="crew-grid">
          {crews.map(crew => (
            <div
              key={crew.id}
              className="crew-card"
              onClick={() => onSelect(crew)}
            >
              <div className="crew-card-header">
                <div className="crew-card-name">{crew.name}</div>
                <span className="crew-member-badge">
                  {crew.members.length}/6
                </span>
              </div>
              {crew.shipName && (
                <div className="crew-card-ship">🚀 {crew.shipName}</div>
              )}
              {crew.missionStatement && (
                <div className="crew-card-mission">{crew.missionStatement}</div>
              )}
              <div className="crew-card-members">
                {crew.members.length === 0 ? (
                  <span className="crew-no-members">No members yet</span>
                ) : (
                  crew.members.map(m => (
                    <span key={m.id} className="crew-member-tag">{m.callSign}</span>
                  ))
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
