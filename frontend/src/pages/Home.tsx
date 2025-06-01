import React from "react";
import { useNavigate } from 'react-router-dom';
import UploadIcon from "@mui/icons-material/Upload";
import CodeIcon from "@mui/icons-material/Code";
import ChecklistIcon from "@mui/icons-material/Checklist";
import EmojiEventsIcon from "@mui/icons-material/EmojiEvents";

const HomePage: React.FC = () => {
  const navigate = useNavigate();

  const handleStart = () => {
    navigate('/grading');
  };

  const steps = [
    {
      icon: <UploadIcon fontSize="large" />,
      title: "Upload",
      desc: "Upload your files",
    },
    {
      icon: <CodeIcon fontSize="large" />,
      title: "Compare",
      desc: "AI compares automatically",
    },
    {
      icon: <ChecklistIcon fontSize="large" />,
      title: "Score",
      desc: "Scores your performance",
    },
    {
      icon: <EmojiEventsIcon fontSize="large" />,
      title: "Rank",
      desc: "Compete with others",
    },
  ];

  return (
    <div
      style={{
        maxWidth: "1200px",
        margin: "0 auto",
        padding: "2rem 1rem",
        textAlign: "center",
      }}
    >
      <h1 style={{ fontSize: "3rem", fontWeight: "700" }}>Gakkle</h1>
      <p style={{ color: "#555" }}>Gakkle provides AI-based image evaluation</p>
      <button
        onClick={handleStart}
        style={{
          padding: "0.5rem 1rem",
          backgroundColor: "#007bff",
          color: "white",
          border: "none",
          borderRadius: "4px",
          cursor: "pointer",
          marginTop: "1rem",
        }}
      >
        Get Started
      </button>

      <div style={{ marginTop: "4rem" }}>
        <h2>How it works?</h2>
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            flexWrap: "wrap",
            gap: "3rem",
            marginTop: "2rem",
          }}
        >
          {steps.map((item, idx) => (
            <div key={idx} style={{ width: "200px" }}>
              <div>{item.icon}</div>
              <h4>{item.title}</h4>
              <p style={{ fontSize: "0.9rem", color: "#666" }}>{item.desc}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default HomePage;
