import React from 'react';

const NotFound = () => {
  return (
    <div style={styles.container}>
      <div style={styles.icon}>⚠️</div>
      <h1 style={styles.title}>Error!</h1>
      <p style={styles.message}>Something went wrong...</p>
    </div>
  );
};

const styles: { [key: string]: React.CSSProperties } = {
  container: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100vh',
    fontFamily: 'sans-serif',
    backgroundColor: '#f8f8f8',
  },
  icon: {
    fontSize: '5rem',
    marginBottom: '1rem',
  },
  title: {
    fontSize: '2rem',
    fontWeight: 'bold',
    marginBottom: '0.5rem',
  },
  message: {
    fontSize: '1.2rem',
  },
};

export default NotFound;
