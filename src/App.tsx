
import './App.css';

import { useEffect, useRef, useState } from 'react';

import { musicTracks, puzzles } from './config';
import JigsawPuzzle from './JigsawPuzzle';

function App() {
  const handlePrev = () => {
    if (currentPuzzle > 0 && puzzleStarted) {
      setCurrentPuzzle(currentPuzzle - 1);
      setPuzzleStarted(false);
    }
  };
  const [showModal, setShowModal] = useState(false);
  const [currentPuzzle, setCurrentPuzzle] = useState(0);
  const [completed, setCompleted] = useState(Array(puzzles.length).fill(false));
  const [musicMuted, setMusicMuted] = useState(false);
  const [musicVolume, setMusicVolume] = useState(1);
  const [musicUrl] = useState(() => musicTracks[Math.floor(Math.random() * musicTracks.length)]);
  const [musicStarted, setMusicStarted] = useState(false);
  const [puzzleStarted, setPuzzleStarted] = useState(false);
  const audioRef = useRef<HTMLAudioElement>(null);

  useEffect(() => {
    if (audioRef.current) {
      audioRef.current.muted = musicMuted;
      audioRef.current.volume = musicVolume;
      if (!puzzleStarted || completed[currentPuzzle]) {
        audioRef.current.pause();
        audioRef.current.currentTime = 0;
      }
    }
  }, [musicMuted, musicVolume, puzzleStarted, completed, currentPuzzle]);

  // Start music after first user interaction
  const startMusic = () => {
    if (!musicStarted && audioRef.current) {
      audioRef.current.play().catch(() => {});
      setMusicStarted(true);
    }
  };

  const handleStartPuzzle = () => {
    setPuzzleStarted(true);
    startMusic();
  };

  const handleComplete = () => {
    setCompleted((prev) => {
      const updated = [...prev];
      updated[currentPuzzle] = true;
      return updated;
    });
  };

  const handleNext = () => {
    if (currentPuzzle < puzzles.length - 1 && completed[currentPuzzle]) {
      setCurrentPuzzle(currentPuzzle + 1);
      setPuzzleStarted(false);
    }
  };


  const handleVolumeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const vol = Number(e.target.value);
    setMusicVolume(vol);
    setMusicMuted(vol === 0);
    startMusic();
  };

  return (
    <div className="jigsaw-app">
      <h1>Jigsaw Puzzle Game</h1>
      <div className="button-group">
        <button className="resource-btn" onClick={() => setShowModal(true)}>Resources</button>
        <button
          className="control-btn"
          onClick={handlePrev}
          disabled={
            !puzzleStarted || currentPuzzle === 0 || !puzzles[currentPuzzle - 1]
          }
        >
          Previous Puzzle
        </button>
        <button
          className="control-btn"
          onClick={handleNext}
          disabled={
            !puzzleStarted || !completed[currentPuzzle] || currentPuzzle === puzzles.length - 1 || !puzzles[currentPuzzle + 1]
          }
        >
          Next Puzzle
        </button>
        {puzzleStarted && !completed[currentPuzzle] && (
          <div className="volume-slider-group">
            <label htmlFor="music-volume" style={{ color: '#fff', marginRight: 8 }}>
              {musicMuted || musicVolume === 0 ? (
                <span
                  style={{ display: 'inline-flex', alignItems: 'center', gap: 4, cursor: 'pointer' }}
                  onClick={() => {
                    setMusicMuted(false);
                    setMusicVolume(1);
                    startMusic();
                  }}
                  title="Unmute"
                >
                  <span role="img" aria-label="Muted" style={{fontSize: '1.2em'}}>🔇</span>
                </span>
              ) : (
                <span
                  style={{ display: 'inline-flex', alignItems: 'center', gap: 4, cursor: 'pointer' }}
                  onClick={() => {
                    setMusicMuted(true);
                    setMusicVolume(0);
                    startMusic();
                  }}
                  title="Mute"
                >
                  <span role="img" aria-label="Volume" style={{fontSize: '1.2em'}}>🔊</span>
                </span>
              )}
            </label>
            <input
              id="music-volume"
              type="range"
              min={0}
              max={1}
              step={0.01}
              value={musicMuted ? 0 : musicVolume}
              onChange={handleVolumeChange}
              style={{ width: 120 }}
            />
          </div>
        )}
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h2>Resources</h2>
            <div style={{marginBottom: '1em'}}>
              <strong>Images:</strong><br />
              <a href="https://science.nasa.gov/gallery/hubbles-universe/" target="_blank" rel="noopener noreferrer">
                NASA Hubble’s Universe Gallery
              </a>
            </div>
            <div style={{marginBottom: '1em'}}>
              <strong>Music:</strong><br />
              <a href="https://pixabay.com/music/search/classical/" target="_blank" rel="noopener noreferrer">
                Pixabay Classical Music
              </a>
            </div>
            <button className="resource-btn" onClick={() => setShowModal(false)} style={{marginTop: '0.5em'}}>Close</button>
          </div>
        </div>
        // ...existing code...
      )}
      <div className="main-flex">
        <div className="puzzle-container-responsive">
          {!puzzleStarted ? (
            <button className="start-btn" onClick={handleStartPuzzle}>
              Start Puzzle
            </button>
          ) : (
            <JigsawPuzzle
              image={puzzles[currentPuzzle].image}
              size={puzzles[currentPuzzle].size}
              onComplete={handleComplete}
              completed={completed[currentPuzzle]}
              areaSize={undefined}
            />
          )}
        </div>
        {/* ...no controls here, all in button group above... */}
      </div>
      <audio ref={audioRef} src={musicUrl} loop />
    </div>
  );
}

export default App;
