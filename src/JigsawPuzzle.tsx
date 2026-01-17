import React, { useState } from 'react';

type JigsawPuzzleProps = {
  image: string;
  size?: number; // number of pieces per row/col
  onComplete: () => void;
  completed: boolean;
  areaSize?: number; // pixel size of puzzle area (optional, undefined = responsive)
};

type Piece = {
  row: number;
  col: number;
  idx: number;
};

// Helper to shuffle an array
function shuffle<T>(arr: T[]): T[] {
  const a = [...arr];
  for (let i = a.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [a[i], a[j]] = [a[j], a[i]];
  }
  return a;
}

const JigsawPuzzle: React.FC<JigsawPuzzleProps> = ({ image, size = 3, onComplete, completed, areaSize }) => {
  // Generate piece data
  const pieces: Piece[] = [];
  for (let row = 0; row < size; row++) {
    for (let col = 0; col < size; col++) {
      pieces.push({ row, col, idx: row * size + col });
    }
  }

  // State: shuffled order of pieces
  const [order, setOrder] = useState<number[]>(() => shuffle(pieces.map((p) => p.idx)));
  const [draggedIdx, setDraggedIdx] = useState<number | null>(null);

  // Check for completion
  React.useEffect(() => {
    if (order.every((idx, i) => idx === i)) {
      onComplete();
    }
    // eslint-disable-next-line
  }, [order]);

  // Drag and drop handlers
  const handleDragStart = (i: number) => setDraggedIdx(i);
  const handleDrop = (i: number) => {
    if (draggedIdx === null || completed) return;
    const newOrder = [...order];
    [newOrder[i], newOrder[draggedIdx]] = [newOrder[draggedIdx], newOrder[i]];
    setOrder(newOrder);
    setDraggedIdx(null);
  };
  const handleDragOver = (e: React.DragEvent) => e.preventDefault();

  // Render grid of pieces and completion message
  // Responsive area size: fit to viewport, max 90vw/80vh
  const computedAreaSize = areaSize || Math.min(window.innerWidth * 0.9, window.innerHeight * 0.8);
  return (
    <div style={{ position: 'relative', width: computedAreaSize, height: computedAreaSize + 20, margin: '0 auto' }}>
      <div
        style={{
          display: 'grid',
          gridTemplateRows: `repeat(${size}, 1fr)`,
          gridTemplateColumns: `repeat(${size}, 1fr)`,
          gap: 2,
          width: computedAreaSize,
          height: computedAreaSize,
          opacity: completed ? 0.5 : 1,
          pointerEvents: completed ? 'none' : 'auto',
        }}
        aria-label="Jigsaw puzzle board"
      >
        {order.map((pieceIdx, i) => {
          const piece = pieces[pieceIdx];
          const pieceSize = computedAreaSize / size;
          return (
            <div
              key={i}
              draggable={!completed}
              onDragStart={() => handleDragStart(i)}
              onDrop={() => handleDrop(i)}
              onDragOver={handleDragOver}
              style={{
                width: pieceSize,
                height: pieceSize,
                backgroundImage: `url(${image})`,
                backgroundSize: `${size * 100}% ${size * 100}%`,
                backgroundPosition: `${(piece.col / (size - 1)) * 100}% ${(piece.row / (size - 1)) * 100}%`,
                border: '1px solid #333',
                boxSizing: 'border-box',
                cursor: completed ? 'default' : 'grab',
                opacity: draggedIdx === i ? 0.5 : 1,
                transition: 'opacity 0.2s',
                outline: draggedIdx === i ? '2px solid #61dafb' : 'none',
              }}
              aria-label={`Puzzle piece ${i + 1}`}
              tabIndex={0}
            />
          );
        })}
      </div>
      {completed && (
        <div
          style={{
            position: 'absolute',
            top: 0,
            left: 0,
            width: '100%',
            height: computedAreaSize,
            background: 'rgba(0,0,0,0.6)',
            color: '#fff',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '2rem',
            fontWeight: 700,
            borderRadius: 8,
            zIndex: 2,
          }}
        >
          🎉 Puzzle Completed!
        </div>
      )}
    </div>
  );
};

export default JigsawPuzzle;
