import type { Crew, SpaceDeveloper } from './types';
import { SENIORITY_LABELS } from './types';

interface CrewDetailProps {
  crew: Crew;
  allDevs: SpaceDeveloper[];
  onJoin: (crewId: number, devId: number) => void;
  onLeave: (crewId: number, devId: number) => void;
  onDelete: (id: number) => void;
  onClose: () => void;
}

export default function CrewDetail({ crew, allDevs, onJoin, onLeave, onDelete, onClose }: CrewDetailProps) {
  const memberIds = new Set(crew.members.map(m => m.id));
  const availableDevs = allDevs.filter(d => d.id && !memberIds.has(d.id));
  const isFull = crew.members.length >= 6;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal crew-detail-modal" onClick={e => e.stopPropagation()}>
        <h2>👥 {crew.name}</h2>

        {crew.missionStatement && (
          <div className="crew-mission-statement">
            <div className="card-section-label">Mission Statement</div>
            <p>{crew.missionStatement}</p>
          </div>
        )}

        {crew.shipName && (
          <div className="crew-ship-name">🚀 {crew.shipName}</div>
        )}

        <div className="crew-member-count">
          {crew.members.length} / 6 members
        </div>

        <div className="card-section-label" style={{ marginTop: '1rem' }}>Crew Roster</div>

        {crew.members.length === 0 ? (
          <div className="empty-state" style={{ padding: '1rem' }}>
            <p>No members yet. Add someone to this crew!</p>
          </div>
        ) : (
          <div className="crew-members-grid">
            {crew.members.map(member => (
              <div key={member.id} className="crew-member-card">
                <div className="crew-member-info">
                  <div className="call-sign" style={{ fontSize: '0.9rem' }}>{member.callSign}</div>
                  <div className="real-name">{member.realName}</div>
                  <span className="seniority-badge" style={{ fontSize: '0.55rem', marginTop: '0.3rem', display: 'inline-block' }}>
                    {SENIORITY_LABELS[member.seniority]}
                  </span>
                </div>
                <button
                  className="btn btn-danger"
                  style={{ padding: '0.3rem 0.6rem', fontSize: '0.6rem' }}
                  onClick={() => member.id && crew.id && onLeave(crew.id, member.id)}
                >
                  Remove
                </button>
              </div>
            ))}
          </div>
        )}

        {!isFull && availableDevs.length > 0 && (
          <div style={{ marginTop: '1rem' }}>
            <div className="card-section-label">Add Member</div>
            <div className="crew-available-devs">
              {availableDevs.map(dev => (
                <button
                  key={dev.id}
                  className="btn crew-add-btn"
                  onClick={() => dev.id && crew.id && onJoin(crew.id, dev.id)}
                >
                  + {dev.callSign}
                </button>
              ))}
            </div>
          </div>
        )}

        {isFull && (
          <div className="crew-full-notice">⚠️ Crew is at maximum capacity (6/6)</div>
        )}

        <div className="form-actions" style={{ marginTop: '1.5rem' }}>
          <button
            className="btn btn-danger"
            onClick={() => {
              if (crew.id) onDelete(crew.id);
            }}
          >
            Disband Crew
          </button>
          <button className="btn" onClick={onClose}>Close</button>
        </div>
      </div>
    </div>
  );
}
