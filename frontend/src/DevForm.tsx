import { useState } from 'react';
import type { SpaceDeveloper, Seniority } from './types';
import { SENIORITY_LABELS } from './types';
import { generateJoke, generateCallSign } from './api';

const EMPTY_DEV: SpaceDeveloper = {
  callSign: '',
  realName: '',
  seniority: 'MASS_OF_SPACE_DUST',
  skills: [],
  ossProjects: [],
  favoriteDevJoke: '',
  coffeesPerDayInLiters: 0,
  debuggingPowerLevel: 0,
  rubberDuckName: '',
  favoriteKeyboardShortcut: '',
  gitCommitStreak: 0,
  stackOverflowReputation: 0,
  stillUsesVim: false,
  shipName: '',
};

interface DevFormProps {
  dev?: SpaceDeveloper;
  onSave: (dev: SpaceDeveloper) => void;
  onCancel: () => void;
}

export default function DevForm({ dev, onSave, onCancel }: DevFormProps) {
  const [form, setForm] = useState<SpaceDeveloper>(dev ?? { ...EMPTY_DEV });
  const [skillsInput, setSkillsInput] = useState(form.skills.join(', '));
  const [ossInput, setOssInput] = useState(form.ossProjects.join(', '));
  const [generatingJoke, setGeneratingJoke] = useState(false);
  const [generatingCallSign, setGeneratingCallSign] = useState(false);

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    onSave({
      ...form,
      skills: skillsInput.split(',').map(s => s.trim()).filter(Boolean),
      ossProjects: ossInput.split(',').map(s => s.trim()).filter(Boolean),
    });
  }

  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <h2>{dev ? '✏️ Edit Space Dev' : '🚀 Register New Space Dev'}</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>Call Sign *</label>
              <input
                required
                value={form.callSign}
                onChange={e => setForm({ ...form, callSign: e.target.value })}
                placeholder="NebulaNinja"
              />
              <button
                type="button"
                className="btn"
                disabled={generatingCallSign}
                style={{ marginTop: '0.5rem', width: '100%' }}
                onClick={async () => {
                  setGeneratingCallSign(true);
                  try {
                    const skills = skillsInput.split(',').map(s => s.trim()).filter(Boolean);
                    const callSign = await generateCallSign(skills, form.seniority);
                    setForm(f => ({ ...f, callSign }));
                  } catch {
                    // silently keep current value
                  } finally {
                    setGeneratingCallSign(false);
                  }
                }}
              >
                {generatingCallSign ? '🛸 Generating...' : '🎲 Generate Call Sign'}
              </button>
            </div>
            <div className="form-group">
              <label>Real Name *</label>
              <input
                required
                value={form.realName}
                onChange={e => setForm({ ...form, realName: e.target.value })}
                placeholder="Alex Starfield"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Seniority (Gravitational Class)</label>
              <select
                value={form.seniority}
                onChange={e => setForm({ ...form, seniority: e.target.value as Seniority })}
              >
                {Object.entries(SENIORITY_LABELS).map(([key, label]) => (
                  <option key={key} value={key}>{label}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Ship Name</label>
              <input
                value={form.shipName}
                onChange={e => setForm({ ...form, shipName: e.target.value })}
                placeholder="Millennium Falsy"
              />
            </div>
          </div>

          <div className="form-group">
            <label>Skills (comma-separated)</label>
            <input
              value={skillsInput}
              onChange={e => setSkillsInput(e.target.value)}
              placeholder="Java, TypeScript, Telepathy"
            />
          </div>

          <div className="form-group">
            <label>OSS Projects (comma-separated)</label>
            <input
              value={ossInput}
              onChange={e => setOssInput(e.target.value)}
              placeholder="nebula-orm, star-cli"
            />
          </div>

          <div className="form-group">
            <label>Favorite Dev Joke</label>
            <textarea
              value={form.favoriteDevJoke}
              onChange={e => setForm({ ...form, favoriteDevJoke: e.target.value })}
              placeholder="Why do Java devs wear glasses?..."
            />
            <button
              type="button"
              className="btn"
              disabled={generatingJoke}
              style={{ marginTop: '0.5rem', width: '100%' }}
              onClick={async () => {
                setGeneratingJoke(true);
                try {
                  const joke = await generateJoke(
                    form.callSign || 'SpaceDev',
                    skillsInput || 'coding'
                  );
                  setForm(f => ({ ...f, favoriteDevJoke: joke }));
                } catch {
                  setForm(f => ({ ...f, favoriteDevJoke: 'AI joke generator is orbiting... try again!' }));
                } finally {
                  setGeneratingJoke(false);
                }
              }}
            >
              {generatingJoke ? '🛸 Generating...' : '🤖 Generate with AI'}
            </button>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>☕ Coffees/Day (Liters)</label>
              <input
                type="number"
                min={0}
                max={99}
                value={form.coffeesPerDayInLiters}
                onChange={e => setForm({ ...form, coffeesPerDayInLiters: +e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>⚡ Debugging Power Level</label>
              <input
                type="number"
                min={0}
                max={9001}
                value={form.debuggingPowerLevel}
                onChange={e => setForm({ ...form, debuggingPowerLevel: +e.target.value })}
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>🦆 Rubber Duck Name</label>
              <input
                value={form.rubberDuckName}
                onChange={e => setForm({ ...form, rubberDuckName: e.target.value })}
                placeholder="Quacksworth III"
              />
            </div>
            <div className="form-group">
              <label>⌨️ Favorite Shortcut</label>
              <input
                value={form.favoriteKeyboardShortcut}
                onChange={e => setForm({ ...form, favoriteKeyboardShortcut: e.target.value })}
                placeholder="Ctrl+Z"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>🔥 Git Commit Streak</label>
              <input
                type="number"
                min={0}
                value={form.gitCommitStreak}
                onChange={e => setForm({ ...form, gitCommitStreak: +e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>📊 Stack Overflow Rep</label>
              <input
                type="number"
                min={0}
                value={form.stackOverflowReputation}
                onChange={e => setForm({ ...form, stackOverflowReputation: +e.target.value })}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="checkbox-label">
              <input
                type="checkbox"
                checked={form.stillUsesVim}
                onChange={e => setForm({ ...form, stillUsesVim: e.target.checked })}
              />
              Still Uses Vim (respect++)
            </label>
          </div>

          <div className="form-actions">
            <button type="button" className="btn" onClick={onCancel}>Cancel</button>
            <button type="submit" className="btn btn-primary">
              {dev ? 'Update Coordinates' : 'Launch Into Registry'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
