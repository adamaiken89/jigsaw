# Jigsaw Puzzle Web App

This app is a jigsaw puzzle game built with React, TypeScript, and Vite. It uses locally stored NASA images and classical music tracks. Puzzles unlock sequentially, and a random music track plays in the background (with mute option only).

## Features
- Sequentially unlocked puzzles using NASA images
- Random classical music track per session (mute only)
- No timer or score
- Responsive UI

## Setup
1. Place your NASA images in `public/assets/images/` (e.g., `image1.jpg`, `image2.jpg`, ...).
2. Place your classical music tracks in `public/assets/music/` (e.g., `track1.mp3`, `track2.mp3`, ...).
3. Run `npm install` to install dependencies.
4. Run `npm run dev` to start the development server.

## Attribution
- Images: NASA Hubble’s Universe Gallery (royalty-free)
- Music: Free Music Archive, Classical genre (royalty-free)

## License
This project is for educational/demo purposes. Ensure you comply with asset licenses if distributing.
    },
  },
])
```

You can also install [eslint-plugin-react-x](https://github.com/Rel1cx/eslint-react/tree/main/packages/plugins/eslint-plugin-react-x) and [eslint-plugin-react-dom](https://github.com/Rel1cx/eslint-react/tree/main/packages/plugins/eslint-plugin-react-dom) for React-specific lint rules:

```js
// eslint.config.js
import reactX from 'eslint-plugin-react-x'
import reactDom from 'eslint-plugin-react-dom'

export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      // Other configs...
      // Enable lint rules for React
      reactX.configs['recommended-typescript'],
      // Enable lint rules for React DOM
      reactDom.configs.recommended,
    ],
    languageOptions: {
      parserOptions: {
        project: ['./tsconfig.node.json', './tsconfig.app.json'],
        tsconfigRootDir: import.meta.dirname,
      },
      // other options...
    },
  },
])
```
