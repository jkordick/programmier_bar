import { useState } from 'react';

interface CrewFormProps {
  onSave: (crew: { name: string; missionStatement: string; shipName: string }) => void;
  onCancel: () => void;
}

export default function CrewForm({ onSave, onCancel }: CrewFormProps) {
  const [name, setName] = useState('');
  const [missionStatement, setMissionStatement] = useState('');
  const [shipName, setShipName] = useState('');

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    onSave({ name, missionStatement, shipName });
  }

  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <h2>🚀 Create New Crew</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Crew Name *</label>
            <input
              required
              value={name}
              onChange={e => setName(e.target.value)}
              placeholder="Nebula Knights"
            />
          </div>
          <div className="form-group">
            <label>Mission Statement</label>
            <textarea
              value={missionStatement}
              onChange={e => setMissionStatement(e.target.value)}
              placeholder="Our mission is to boldly code where no dev has coded before..."
            />
          </div>
          <div className="form-group">
            <label>Ship Name</label>
            <input
              value={shipName}
              onChange={e => setShipName(e.target.value)}
              placeholder="USS Refactor"
            />
          </div>
          <div className="form-actions">
            <button type="button" className="btn" onClick={onCancel}>Cancel</button>
            <button type="submit" className="btn btn-primary">Launch Crew</button>
          </div>
        </form>
      </div>
    </div>
  );
}
