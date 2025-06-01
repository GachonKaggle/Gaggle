import React, { useRef, useState } from 'react';
import '../styles/TaskSubmitModal.css';

type Props = {
  show: boolean;
  onClose: () => void;
  onSubmit: (file: File, task: string) => void; // task로 변경!
  taskInfo?: any;
};

const TaskSubmitModal: React.FC<Props> = ({ show, onClose, onSubmit }) => {
  const [file, setFile] = useState<File | null>(null);
  const [task, setTask] = useState('');
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  if (!show) return null;

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selected = e.target.files?.[0] ?? null;
    setFile(selected);
  };

  const handleTaskChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTask(e.target.value);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) {
      alert('Please select a zip file.');
      return;
    }
    if (!task.trim()) {
      alert('Please enter the task name.');
      return;
    }
    onSubmit(file, task);
    setFile(null);
    setTask('');
    // Reset input's value for user experience
    if (fileInputRef.current) fileInputRef.current.value = '';
  };

  return (
    <div className="modal-backdrop">
      <div className="modal-card">
        <button className="close-btn" onClick={onClose} aria-label="Close">&times;</button>
        <h2>📦 Submit Task</h2>
        <form className="modal-form" onSubmit={handleSubmit} autoComplete="off">
          <label className="modal-label">
            <span>Task Name</span>
            <input
              type="text"
              value={task}
              onChange={handleTaskChange}
              placeholder="Enter task name"
              className="modal-input"
              autoFocus
              maxLength={30}
              name="task"
              required
            />
          </label>
          <label className="modal-label" style={{ marginTop: 15 }}>
            <span>ZIP File</span>
            <input
              type="file"
              accept=".zip"
              onChange={handleFileChange}
              className="modal-input"
              ref={fileInputRef}
              name="file"
              required
            />
          </label>
          <button className="modal-submit" type="submit">Submit</button>
        </form>
        <div className="modal-footer">
          <button className="modal-cancel" onClick={onClose} type="button">Cancel</button>
        </div>
      </div>
    </div>
  );
};

export default TaskSubmitModal;
