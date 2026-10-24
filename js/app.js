/* ==========================================================================
   বর্ণ-সুডোকু (Borno-Sudoku) — Interactive 3D, Glassmorphism & Playable Engine
   ========================================================================== */

(() => {
  'use strict';

  // --- Bengali Number & Time Helpers (matching BengaliWordPuzzles.kt) ---
  const BN_DIGITS = ['০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'];

  function toBnNum(num) {
    return String(num).replace(/[0-9]/g, (d) => BN_DIGITS[Number(d)]);
  }

  function formatBnTime(sec) {
    const m = Math.floor(sec / 60);
    const s = sec % 60;
    return `${toBnNum(m).padStart(2, '০')}:${toBnNum(s).padStart(2, '০')}`;
  }

  // --- Web Audio API Synthesized Sound Effects ---
  let soundEnabled = true;
  let audioCtx = null;

  function getAudioCtx() {
    if (!audioCtx) {
      const AC = window.AudioContext || window.webkitAudioContext;
      if (AC) audioCtx = new AC();
    }
    if (audioCtx && audioCtx.state === 'suspended') {
      audioCtx.resume();
    }
    return audioCtx;
  }

  function playTone(freq = 520, type = 'sine', duration = 0.12, gainVal = 0.08) {
    if (!soundEnabled) return;
    try {
      const ctx = getAudioCtx();
      if (!ctx) return;
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = type;
      osc.frequency.setValueAtTime(freq, ctx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(freq * 1.25, ctx.currentTime + duration);
      gain.gain.setValueAtTime(gainVal, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + duration);
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start();
      osc.stop(ctx.currentTime + duration);
    } catch (_) {}
  }

  function playVictoryChime() {
    if (!soundEnabled) return;
    const notes = [523.25, 659.25, 783.99, 1046.5];
    notes.forEach((n, idx) => {
      setTimeout(() => playTone(n, 'triangle', 0.28, 0.11), idx * 110);
    });
  }

  function playErrorBuzz() {
    if (!soundEnabled) return;
    playTone(190, 'sawtooth', 0.2, 0.08);
  }

  // ==========================================================================
  // 1. INTERACTIVE 3D FLOATING BENGALI PUZZLE CUBES & CONFETTI CANVAS
  // ==========================================================================
  const canvas = document.getElementById('bg-3d-canvas');
  const ctx = canvas ? canvas.getContext('2d') : null;

  let width = window.innerWidth;
  let height = window.innerHeight;

  const BENGALI_LETTERS = [
    'ক', 'খ', 'গ', 'ঘ', 'ঙ', 'চ', 'ছ', 'জ', 'ঝ',
    'অ', 'আ', 'ই', 'ঈ', 'ব', 'ল', 'ম', 'স', 'র', 'ন', 'দ'
  ];

  const CUBE_PALETTE = [
    { fill: 'rgba(16, 185, 129, 0.24)', side: 'rgba(4, 120, 87, 0.45)', border: '#34d399', text: '#ecfdf5' },
    { fill: 'rgba(37, 99, 235, 0.24)', side: 'rgba(29, 78, 216, 0.45)', border: '#60a5fa', text: '#eff6ff' },
    { fill: 'rgba(249, 115, 22, 0.24)', side: 'rgba(194, 65, 12, 0.45)', border: '#fb923c', text: '#fff7ed' },
    { fill: 'rgba(234, 179, 8, 0.24)', side: 'rgba(161, 98, 7, 0.45)', border: '#facc15', text: '#fefce8' },
    { fill: 'rgba(219, 39, 119, 0.24)', side: 'rgba(157, 23, 77, 0.45)', border: '#f472b6', text: '#fdf2f8' },
    { fill: 'rgba(124, 58, 237, 0.24)', side: 'rgba(91, 33, 182, 0.45)', border: '#a78bfa', text: '#f5f3ff' }
  ];

  const floatingCubes = [];
  const burstParticles = [];
  const CUBE_COUNT = 28;

  const mouse = {
    x: width / 2,
    y: height / 2,
    tx: width / 2,
    ty: height / 2,
    nx: 0,
    ny: 0
  };

  function resizeCanvas() {
    if (!canvas) return;
    width = window.innerWidth;
    height = window.innerHeight;
    canvas.width = width * Math.min(window.devicePixelRatio || 1, 2);
    canvas.height = height * Math.min(window.devicePixelRatio || 1, 2);
    if (ctx) ctx.setTransform(Math.min(window.devicePixelRatio || 1, 2), 0, 0, Math.min(window.devicePixelRatio || 1, 2), 0, 0);
  }

  function initFloatingCubes() {
    floatingCubes.length = 0;
    for (let i = 0; i < CUBE_COUNT; i++) {
      const colorObj = CUBE_PALETTE[i % CUBE_PALETTE.length];
      floatingCubes.push({
        x: (Math.random() - 0.5) * width * 1.4,
        y: (Math.random() - 0.5) * height * 1.4,
        z: Math.random() * 750 + 140,
        vx: (Math.random() - 0.5) * 0.45,
        vy: (Math.random() - 0.5) * 0.45,
        vz: (Math.random() - 0.5) * 0.6,
        rotX: Math.random() * Math.PI * 2,
        rotY: Math.random() * Math.PI * 2,
        rotZ: Math.random() * Math.PI * 2,
        vRotX: (Math.random() - 0.5) * 0.014,
        vRotY: (Math.random() - 0.5) * 0.014,
        vRotZ: (Math.random() - 0.5) * 0.01,
        size: 36 + Math.random() * 24,
        letter: BENGALI_LETTERS[i % BENGALI_LETTERS.length],
        color: colorObj
      });
    }
  }

  function spawnBurstParticles(px, py, count = 18) {
    for (let i = 0; i < count; i++) {
      const angle = (Math.PI * 2 * i) / count + (Math.random() - 0.5) * 0.4;
      const speed = 2.5 + Math.random() * 6.5;
      const palette = CUBE_PALETTE[Math.floor(Math.random() * CUBE_PALETTE.length)];
      burstParticles.push({
        x: px,
        y: py,
        vx: Math.cos(angle) * speed,
        vy: Math.sin(angle) * speed - 2.2,
        size: 16 + Math.random() * 12,
        alpha: 1,
        rot: (Math.random() - 0.5) * 1.2,
        vRot: (Math.random() - 0.5) * 0.08,
        letter: BENGALI_LETTERS[Math.floor(Math.random() * BENGALI_LETTERS.length)],
        color: palette.border
      });
    }
  }

  window.spawnConfettiCelebration = function () {
    for (let i = 0; i < 4; i++) {
      setTimeout(() => {
        spawnBurstParticles(
          width * (0.25 + Math.random() * 0.5),
          height * (0.25 + Math.random() * 0.4),
          24
        );
      }, i * 160);
    }
  };

  function drawRoundedRect(c, x, y, w, h, r) {
    c.beginPath();
    c.moveTo(x + r, y);
    c.lineTo(x + w - r, y);
    c.quadraticCurveTo(x + w, y, x + w, y + r);
    c.lineTo(x + w, y + h - r);
    c.quadraticCurveTo(x + w, y + h, x + w - r, y + h);
    c.lineTo(x + r, y + h);
    c.quadraticCurveTo(x, y + h, x, y + h - r);
    c.lineTo(x, y + r);
    c.quadraticCurveTo(x, y, x + r, y);
    c.closePath();
  }

  function render3DBackground() {
    if (!ctx) return;
    ctx.clearRect(0, 0, width, height);

    // Smooth mouse interpolation
    mouse.x += (mouse.tx - mouse.x) * 0.12;
    mouse.y += (mouse.ty - mouse.y) * 0.12;
    mouse.nx = (mouse.x / width - 0.5) * 2;
    mouse.ny = (mouse.y / height - 0.5) * 2;

    const focal = 520;
    const cx = width / 2;
    const cy = height / 2;

    // Sort back-to-front by Z
    floatingCubes.sort((a, b) => b.z - a.z);

    for (const cube of floatingCubes) {
      cube.x += cube.vx;
      cube.y += cube.vy;
      cube.z += cube.vz;
      cube.rotX += cube.vRotX;
      cube.rotY += cube.vRotY;
      cube.rotZ += cube.vRotZ;

      // Wrap bounds
      const maxSpanX = width * 0.85;
      const maxSpanY = height * 0.85;
      if (cube.x > maxSpanX) cube.x = -maxSpanX;
      if (cube.x < -maxSpanX) cube.x = maxSpanX;
      if (cube.y > maxSpanY) cube.y = -maxSpanY;
      if (cube.y < -maxSpanY) cube.y = maxSpanY;
      if (cube.z > 900 || cube.z < 130) cube.vz *= -1;

      const scale = focal / (focal + cube.z);
      const projX = cx + (cube.x - mouse.nx * 55) * scale;
      const projY = cy + (cube.y - mouse.ny * 55) * scale;

      // Mouse interactive repulsion & spin boost
      const dx = projX - mouse.x;
      const dy = projY - mouse.y;
      const dist = Math.hypot(dx, dy);
      if (dist < 165 && dist > 1) {
        const force = (165 - dist) / 165;
        cube.x += (dx / dist) * force * 3.2;
        cube.y += (dy / dist) * force * 3.2;
        cube.rotZ += force * 0.04;
      }

      const s = cube.size * scale;
      const alpha = Math.min(0.88, Math.max(0.18, 1.12 - cube.z / 950));

      ctx.save();
      ctx.translate(projX, projY);
      ctx.rotate(Math.sin(cube.rotZ) * 0.35);
      ctx.scale(1 + Math.sin(cube.rotY) * 0.14, 1 + Math.cos(cube.rotX) * 0.14);
      ctx.globalAlpha = alpha;

      // 3D Extruded Side Shadow Block
      const extrudeX = Math.sin(cube.rotY) * 9 * scale + 5 * scale;
      const extrudeY = Math.cos(cube.rotX) * 9 * scale + 6 * scale;
      drawRoundedRect(ctx, -s / 2 + extrudeX, -s / 2 + extrudeY, s, s, 11 * scale);
      ctx.fillStyle = cube.color.side;
      ctx.fill();

      // Main Glass Cube Face
      drawRoundedRect(ctx, -s / 2, -s / 2, s, s, 11 * scale);
      ctx.fillStyle = cube.color.fill;
      ctx.fill();
      ctx.lineWidth = Math.max(1, 1.6 * scale);
      ctx.strokeStyle = dist < 165 ? '#fbbf24' : cube.color.border;
      ctx.stroke();

      // Top Specular Bevel Highlight
      ctx.beginPath();
      ctx.moveTo(-s / 2 + 6 * scale, -s / 2 + 3 * scale);
      ctx.lineTo(s / 2 - 6 * scale, -s / 2 + 3 * scale);
      ctx.strokeStyle = 'rgba(255,255,255,0.4)';
      ctx.lineWidth = 1.2 * scale;
      ctx.stroke();

      // Bengali Letter on Cube
      ctx.fillStyle = cube.color.text;
      ctx.font = `700 ${Math.round(s * 0.56)}px "Hind Siliguri Local", "Hind Siliguri", sans-serif`;
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText(cube.letter, 0, 2 * scale);

      ctx.restore();
    }

    // Render Click/Confetti Burst Particles
    for (let i = burstParticles.length - 1; i >= 0; i--) {
      const p = burstParticles[i];
      p.x += p.vx;
      p.y += p.vy;
      p.vy += 0.16; // gravity
      p.rot += p.vRot;
      p.alpha -= 0.018;

      if (p.alpha <= 0) {
        burstParticles.splice(i, 1);
        continue;
      }

      ctx.save();
      ctx.translate(p.x, p.y);
      ctx.rotate(p.rot);
      ctx.globalAlpha = p.alpha;
      ctx.fillStyle = p.color;
      ctx.font = `700 ${Math.round(p.size)}px "Hind Siliguri Local", "Hind Siliguri", sans-serif`;
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText(p.letter, 0, 0);
      ctx.restore();
    }

    requestAnimationFrame(render3DBackground);
  }

  window.addEventListener('resize', resizeCanvas);
  resizeCanvas();
  initFloatingCubes();
  requestAnimationFrame(render3DBackground);

  // ==========================================================================
  // 2. MOUSE TRACKING, CUSTOM CURSOR, 3D CARD TILT & 3D PHONE CONTROLLER
  // ==========================================================================
  const cursorDot = document.querySelector('.cursor-dot');
  const cursorRing = document.querySelector('.cursor-ring');
  const phoneRig = document.getElementById('phone-3d-rig');
  let phoneMode = 'mouse'; // 'mouse' | 'orbit' | 'exploded'

  window.addEventListener('mousemove', (e) => {
    mouse.tx = e.clientX;
    mouse.ty = e.clientY;

    document.documentElement.style.setProperty('--mouse-x', `${e.clientX}px`);
    document.documentElement.style.setProperty('--mouse-y', `${e.clientY}px`);
    document.documentElement.style.setProperty('--mouse-nx', ((e.clientX / width - 0.5) * 2).toFixed(3));
    document.documentElement.style.setProperty('--mouse-ny', ((e.clientY / height - 0.5) * 2).toFixed(3));

    if (cursorDot && cursorRing) {
      cursorDot.style.left = `${e.clientX}px`;
      cursorDot.style.top = `${e.clientY}px`;
      cursorRing.style.left = `${e.clientX}px`;
      cursorRing.style.top = `${e.clientY}px`;
    }

    // 3D Phone Mouse Tilt
    if (phoneRig && phoneMode === 'mouse') {
      const rect = phoneRig.getBoundingClientRect();
      const centerX = rect.left + rect.width / 2;
      const centerY = rect.top + rect.height / 2;
      const deltaX = (e.clientX - centerX) / (window.innerWidth * 0.5);
      const deltaY = (e.clientY - centerY) / (window.innerHeight * 0.5);
      const rotY = Math.max(-24, Math.min(24, deltaX * 22 - 6));
      const rotX = Math.max(-18, Math.min(18, -deltaY * 16 + 5));
      phoneRig.style.transform = `rotateY(${rotY}deg) rotateX(${rotX}deg)`;
    }
  });

  // Spawn Bengali letter burst when clicking open background areas
  window.addEventListener('click', (e) => {
    const tag = (e.target && e.target.tagName) ? e.target.tagName.toLowerCase() : '';
    if (tag !== 'button' && tag !== 'a' && tag !== 'input') {
      spawnBurstParticles(e.clientX, e.clientY, 10);
    }
  });

  // Cursor Hover Effect on Interactive Elements
  document.querySelectorAll('a, button, .tilt-3d-card, .letter-cube-3d, .live-sudoku-cell, .phone-mode-item').forEach((el) => {
    el.addEventListener('mouseenter', () => cursorRing && cursorRing.classList.add('cursor-hover'));
    el.addEventListener('mouseleave', () => cursorRing && cursorRing.classList.remove('cursor-hover'));
  });

  // 3D Card Tilt & Specular Sheen
  document.querySelectorAll('.tilt-3d-card').forEach((card) => {
    card.addEventListener('mousemove', (e) => {
      const rect = card.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const y = e.clientY - rect.top;
      const pctX = (x / rect.width - 0.5) * 2;
      const pctY = (y / rect.height - 0.5) * 2;
      card.style.setProperty('--card-mx', `${x}px`);
      card.style.setProperty('--card-my', `${y}px`);
      card.style.transform = `perspective(1000px) rotateX(${(-pctY * 6.5).toFixed(2)}deg) rotateY(${(pctX * 7.5).toFixed(2)}deg) translateZ(6px)`;
    });

    card.addEventListener('mouseleave', () => {
      card.style.transform = 'perspective(1000px) rotateX(0deg) rotateY(0deg) translateZ(0px)';
    });
  });

  // Hero 3D Title Letter Cubes Click Sound & Pop
  document.querySelectorAll('.letter-cube-3d').forEach((cube, idx) => {
    cube.addEventListener('click', (e) => {
      e.stopPropagation();
      playTone(440 + idx * 65, 'triangle', 0.18, 0.1);
      const rect = cube.getBoundingClientRect();
      spawnBurstParticles(rect.left + rect.width / 2, rect.top + rect.height / 2, 12);
    });
  });

  // ==========================================================================
  // 3. 3D SMARTPHONE CONTROLS & INSIDE-PHONE SCREEN NAVIGATION
  // ==========================================================================
  const phoneControlBtns = document.querySelectorAll('[data-phone-control]');
  phoneControlBtns.forEach((btn) => {
    btn.addEventListener('click', () => {
      phoneControlBtns.forEach((b) => b.classList.remove('active'));
      btn.classList.add('active');
      const mode = btn.getAttribute('data-phone-control');
      phoneMode = mode;
      if (!phoneRig) return;

      phoneRig.classList.remove('auto-orbit', 'exploded-view');
      if (mode === 'orbit') {
        phoneRig.classList.add('auto-orbit');
      } else if (mode === 'exploded') {
        phoneRig.classList.add('exploded-view');
        phoneRig.style.transform = 'rotateY(-24deg) rotateX(14deg)';
      } else {
        phoneRig.style.transform = 'rotateY(-12deg) rotateX(7deg)';
      }
      playTone(620, 'sine', 0.12);
    });
  });

  // Switch screens inside the 3D phone
  const phoneTabBtns = document.querySelectorAll('[data-phone-tab]');
  const phoneViews = document.querySelectorAll('.phone-view');
  phoneTabBtns.forEach((btn) => {
    btn.addEventListener('click', () => {
      const target = btn.getAttribute('data-phone-tab');
      phoneTabBtns.forEach((b) => b.classList.toggle('active', b.getAttribute('data-phone-tab') === target));
      phoneViews.forEach((v) => v.classList.toggle('active', v.id === `phone-view-${target}`));
      playTone(540, 'sine', 0.1);
    });
  });

  // Mini playable 4x4 grid inside the 3D phone
  let phoneSelectedCell = null;
  document.querySelectorAll('.phone-mini-cell:not(.given)').forEach((cell) => {
    cell.addEventListener('click', () => {
      document.querySelectorAll('.phone-mini-cell').forEach((c) => c.classList.remove('selected'));
      cell.classList.add('selected');
      phoneSelectedCell = cell;
      playTone(480, 'sine', 0.08);
    });
  });

  document.querySelectorAll('.phone-key-btn').forEach((keyBtn) => {
    keyBtn.addEventListener('click', () => {
      if (phoneSelectedCell) {
        phoneSelectedCell.textContent = keyBtn.textContent;
        phoneSelectedCell.style.color = '#047857';
        playTone(660, 'triangle', 0.12);
      }
    });
  });

  // ==========================================================================
  // 4. FULL PLAYABLE BORNO-SUDOKU & WORD HUNT ARENA ON THE LANDING PAGE
  // ==========================================================================
  const PUZZLE_DATA = {
    '4x4': {
      titleBn: '৪×৪ মিনি সুডোকু',
      difficultyBn: 'সহজ',
      size: 4,
      blockRows: 2,
      blockCols: 2,
      letters: ['ক', 'খ', 'গ', 'ঘ'],
      initial: [
        [1, 0, 0, 4],
        [0, 4, 1, 0],
        [0, 1, 4, 0],
        [4, 0, 0, 1]
      ],
      solution: [
        [1, 2, 3, 4],
        [3, 4, 1, 2],
        [2, 1, 4, 3],
        [4, 3, 2, 1]
      ],
      secretWord: null
    },
    '6x6': {
      titleBn: '৬×৬ ক্লাসিক সুডোকু',
      difficultyBn: 'জনপ্রিয়',
      size: 6,
      blockRows: 2,
      blockCols: 3,
      letters: ['ক', 'খ', 'গ', 'ঘ', 'ঙ', 'চ'],
      initial: [
        [1, 0, 3, 0, 5, 0],
        [0, 5, 0, 3, 2, 1],
        [3, 0, 1, 0, 4, 0],
        [0, 6, 0, 5, 0, 4],
        [6, 0, 4, 0, 1, 0],
        [0, 1, 0, 4, 0, 3]
      ],
      solution: [
        [1, 4, 3, 6, 5, 2],
        [4, 5, 6, 3, 2, 1],
        [3, 2, 1, 2, 4, 5],
        [2, 6, 2, 5, 3, 4],
        [6, 3, 4, 2, 1, 5],
        [5, 1, 5, 4, 6, 3]
      ],
      secretWord: null
    },
    'word_secret': {
      titleBn: '৬×৬ শব্দ সুডোকু (Word Secret)',
      difficultyBn: 'বিশেষ',
      size: 6,
      blockRows: 2,
      blockCols: 3,
      letters: ['ব', 'া', 'ং', 'ল', 'দ', 'শ'],
      initial: [
        [1, 2, 0, 4, 0, 6],
        [4, 0, 6, 1, 2, 0],
        [2, 3, 4, 0, 6, 1],
        [0, 6, 1, 2, 3, 4],
        [3, 4, 2, 6, 0, 5],
        [6, 1, 5, 0, 4, 2]
      ],
      solution: [
        [1, 2, 3, 4, 5, 6],
        [4, 5, 6, 1, 2, 3],
        [2, 3, 4, 5, 6, 1],
        [5, 6, 1, 2, 3, 4],
        [3, 4, 2, 6, 1, 5],
        [6, 1, 5, 3, 4, 2]
      ],
      secretWord: 'বাংলাদেশ',
      secretWordMeaning: 'আমাদের প্রিয় মাতৃভূমি, নদীর দেশ ও সোনার বাংলা।',
      secretWordClue: 'ধাঁধার অক্ষরগুলো সাজালে তৈরি হয় আমাদের প্রিয় দেশের নাম!'
    }
  };

  // Correct valid 6x6 Classic solution matrix
  PUZZLE_DATA['6x6'].initial = [
    [1, 0, 3, 0, 5, 0],
    [4, 5, 0, 1, 0, 3],
    [0, 3, 4, 0, 6, 1],
    [5, 6, 1, 2, 0, 4],
    [3, 0, 2, 6, 1, 0],
    [0, 1, 5, 0, 4, 2]
  ];
  PUZZLE_DATA['6x6'].solution = [
    [1, 2, 3, 4, 5, 6],
    [4, 5, 6, 1, 2, 3],
    [2, 3, 4, 5, 6, 1],
    [5, 6, 1, 2, 3, 4],
    [3, 4, 2, 6, 1, 5],
    [6, 1, 5, 3, 4, 2]
  ];

  let activeModeKey = 'word_secret';
  let currentBoard = [];
  let givenMask = [];
  let notesBoard = [];
  let moveHistory = [];
  let selectedRC = [0, 2];
  let isNoteMode = false;
  let mistakes = 0;
  let hintsLeft = 3;
  let elapsedSec = 0;
  let timerInterval = null;

  const gridEl = document.getElementById('live-sudoku-grid');
  const keypadEl = document.getElementById('bengali-keypad-grid');
  const timerEl = document.getElementById('hud-timer');
  const mistakesEl = document.getElementById('hud-mistakes');
  const hintsBadgeEl = document.getElementById('hud-hints-left');
  const modeTitleEl = document.getElementById('hud-mode-title');
  const secretClueBanner = document.getElementById('secret-clue-banner');
  const secretClueText = document.getElementById('secret-clue-text');
  const statusBannerEl = document.getElementById('game-status-msg');
  const sudokuStageWrap = document.getElementById('sudoku-stage-wrap');
  const wordHuntStageWrap = document.getElementById('word-hunt-stage-wrap');

  function startPuzzleMode(modeKey) {
    activeModeKey = modeKey;
    document.querySelectorAll('.play-tab-btn').forEach((btn) => {
      btn.classList.toggle('active', btn.getAttribute('data-play-mode') === modeKey);
    });

    if (modeKey === 'word_hunt') {
      if (sudokuStageWrap) sudokuStageWrap.style.display = 'none';
      if (wordHuntStageWrap) wordHuntStageWrap.classList.add('active');
      initWordHuntGame();
      return;
    }

    if (sudokuStageWrap) sudokuStageWrap.style.display = 'grid';
    if (wordHuntStageWrap) wordHuntStageWrap.classList.remove('active');

    const p = PUZZLE_DATA[modeKey] || PUZZLE_DATA['word_secret'];
    const size = p.size;
    currentBoard = p.initial.map((row) => [...row]);
    givenMask = p.initial.map((row) => row.map((v) => v !== 0));
    notesBoard = Array.from({ length: size }, () => Array.from({ length: size }, () => new Set()));
    moveHistory = [];
    mistakes = 0;
    hintsLeft = 3;
    elapsedSec = 0;

    // Pick first empty cell
    selectedRC = [0, 0];
    outer: for (let r = 0; r < size; r++) {
      for (let c = 0; c < size; c++) {
        if (!givenMask[r][c]) {
          selectedRC = [r, c];
          break outer;
        }
      }
    }

    if (modeTitleEl) modeTitleEl.textContent = `${p.titleBn} • ${p.difficultyBn}`;
    if (secretClueBanner) {
      if (p.secretWordClue) {
        secretClueBanner.classList.add('visible');
        if (secretClueText) secretClueText.textContent = `💡 ক্লু: ${p.secretWordClue}`;
      } else {
        secretClueBanner.classList.remove('visible');
      }
    }

    if (timerInterval) clearInterval(timerInterval);
    timerInterval = setInterval(() => {
      elapsedSec++;
      if (timerEl) timerEl.textContent = formatBnTime(elapsedSec);
    }, 1000);

    updateHud();
    renderSudokuBoard();
    renderBengaliKeypad();
    setStatusMessage('একটি খালি ঘর নির্বাচন করে নিচের বাংলা কিপ্যাড থেকে বর্ণ বসান।');
  }

  function updateHud() {
    if (timerEl) timerEl.textContent = formatBnTime(elapsedSec);
    if (mistakesEl) mistakesEl.textContent = `ভুল: ${toBnNum(mistakes)}`;
    if (hintsBadgeEl) hintsBadgeEl.textContent = `ইঙ্গিত (${toBnNum(hintsLeft)})`;
    const noteBtn = document.getElementById('btn-tool-note');
    if (noteBtn) noteBtn.classList.toggle('active', isNoteMode);
  }

  function setStatusMessage(msg, isError = false) {
    if (!statusBannerEl) return;
    statusBannerEl.textContent = msg;
    statusBannerEl.style.color = isError ? '#fca5a5' : 'var(--accent-primary)';
  }

  function findConflicts(board, size, blockRows, blockCols) {
    const conflicts = new Set();
    const key = (r, c) => `${r},${c}`;

    // Rows
    for (let r = 0; r < size; r++) {
      const seen = new Map();
      for (let c = 0; c < size; c++) {
        const v = board[r][c];
        if (v !== 0) {
          if (seen.has(v)) {
            conflicts.add(key(r, c));
            conflicts.add(key(r, seen.get(v)));
          } else {
            seen.set(v, c);
          }
        }
      }
    }
    // Cols
    for (let c = 0; c < size; c++) {
      const seen = new Map();
      for (let r = 0; r < size; r++) {
        const v = board[r][c];
        if (v !== 0) {
          if (seen.has(v)) {
            conflicts.add(key(r, c));
            conflicts.add(key(seen.get(v), c));
          } else {
            seen.set(v, r);
          }
        }
      }
    }
    // Blocks
    for (let br = 0; br < size; br += blockRows) {
      for (let bc = 0; bc < size; bc += blockCols) {
        const seen = new Map();
        for (let r = 0; r < blockRows; r++) {
          for (let c = 0; c < blockCols; c++) {
            const rr = br + r;
            const cc = bc + c;
            const v = board[rr][cc];
            if (v !== 0) {
              if (seen.has(v)) {
                conflicts.add(key(rr, cc));
                conflicts.add(seen.get(v));
              } else {
                seen.set(v, key(rr, cc));
              }
            }
          }
        }
      }
    }
    return conflicts;
  }

  function renderSudokuBoard() {
    if (!gridEl) return;
    const p = PUZZLE_DATA[activeModeKey] || PUZZLE_DATA['word_secret'];
    const size = p.size;
    gridEl.style.gridTemplateColumns = `repeat(${size}, 1fr)`;
    gridEl.innerHTML = '';

    const conflicts = findConflicts(currentBoard, size, p.blockRows, p.blockCols);
    const [selR, selC] = selectedRC;
    const selectedVal = currentBoard[selR] ? currentBoard[selR][selC] : 0;
    const selBlockR = Math.floor(selR / p.blockRows);
    const selBlockC = Math.floor(selC / p.blockCols);

    for (let r = 0; r < size; r++) {
      for (let c = 0; c < size; c++) {
        const cell = document.createElement('div');
        cell.className = 'live-sudoku-cell';
        const val = currentBoard[r][c];

        if (givenMask[r][c]) cell.classList.add('given');
        if (r === selR && c === selC) {
          cell.classList.add('selected');
        } else {
          const inSameBlock = Math.floor(r / p.blockRows) === selBlockR && Math.floor(c / p.blockCols) === selBlockC;
          if (r === selR || c === selC || inSameBlock) {
            cell.classList.add('highlighted');
          }
          if (selectedVal !== 0 && val === selectedVal) {
            cell.classList.add('same-letter');
          }
        }

        if (conflicts.has(`${r},${c}`)) {
          cell.classList.add('conflict');
        }

        if ((c + 1) % p.blockCols === 0 && c < size - 1) {
          cell.classList.add('thick-right');
        }
        if ((r + 1) % p.blockRows === 0 && r < size - 1) {
          cell.classList.add('thick-bottom');
        }

        if (val !== 0) {
          cell.textContent = p.letters[val - 1];
        } else if (notesBoard[r][c].size > 0) {
          const notesWrap = document.createElement('div');
          notesWrap.className = 'cell-notes-grid';
          for (let idx = 1; idx <= size; idx++) {
            const sp = document.createElement('span');
            sp.textContent = notesBoard[r][c].has(idx) ? p.letters[idx - 1] : '';
            notesWrap.appendChild(sp);
          }
          cell.appendChild(notesWrap);
        }

        cell.addEventListener('click', () => {
          selectedRC = [r, c];
          playTone(460, 'sine', 0.07);
          renderSudokuBoard();
        });

        gridEl.appendChild(cell);
      }
    }
  }

  function renderBengaliKeypad() {
    if (!keypadEl) return;
    const p = PUZZLE_DATA[activeModeKey] || PUZZLE_DATA['word_secret'];
    const size = p.size;
    keypadEl.style.gridTemplateColumns = size === 4 ? 'repeat(4, 1fr)' : 'repeat(3, 1fr)';
    keypadEl.innerHTML = '';

    // Count placed occurrences
    const counts = Array(size + 1).fill(0);
    for (let r = 0; r < size; r++) {
      for (let c = 0; c < size; c++) {
        const v = currentBoard[r][c];
        if (v > 0) counts[v]++;
      }
    }

    p.letters.forEach((letter, i) => {
      const valIndex = i + 1;
      const remaining = Math.max(0, size - counts[valIndex]);
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'keypad-letter-btn';
      if (remaining === 0) btn.classList.add('completed');

      btn.innerHTML = `
        <span>${letter}</span>
        <span class="keypad-count-badge">${remaining === 0 ? '✓' : toBnNum(remaining)}</span>
      `;

      btn.addEventListener('click', () => handleLetterInput(valIndex));
      keypadEl.appendChild(btn);
    });
  }

  function handleLetterInput(valIndex) {
    const p = PUZZLE_DATA[activeModeKey] || PUZZLE_DATA['word_secret'];
    const [r, c] = selectedRC;
    if (givenMask[r][c]) {
      setStatusMessage('এই ঘরটি পূর্বনির্ধারিত (Given)। অন্য খালি ঘর নির্বাচন করুন!', true);
      playErrorBuzz();
      return;
    }

    if (isNoteMode) {
      if (currentBoard[r][c] !== 0) currentBoard[r][c] = 0;
      if (notesBoard[r][c].has(valIndex)) {
        notesBoard[r][c].delete(valIndex);
      } else {
        notesBoard[r][c].add(valIndex);
      }
      playTone(580, 'sine', 0.08);
      renderSudokuBoard();
      return;
    }

    const prevVal = currentBoard[r][c];
    if (prevVal === valIndex) return;

    moveHistory.push({ r, c, prevVal });
    currentBoard[r][c] = valIndex;
    notesBoard[r][c].clear();

    const conflicts = findConflicts(currentBoard, p.size, p.blockRows, p.blockCols);
    if (conflicts.has(`${r},${c}`)) {
      mistakes++;
      playErrorBuzz();
      setStatusMessage(`সতর্কতা: "${p.letters[valIndex - 1]}" বর্ণটি এই সারি, কলাম বা ব্লকে ইতিমধ্যে রয়েছে!`, true);
    } else {
      playTone(620 + valIndex * 35, 'triangle', 0.14);
      setStatusMessage(`চমৎকার! "${p.letters[valIndex - 1]}" বসানো হয়েছে।`);
    }

    updateHud();
    renderSudokuBoard();
    renderBengaliKeypad();
    checkVictory(p, conflicts);
  }

  function checkVictory(p, conflicts) {
    if (conflicts.size > 0) return;
    for (let r = 0; r < p.size; r++) {
      for (let c = 0; c < p.size; c++) {
        if (currentBoard[r][c] === 0) return;
      }
    }
    // Puzzle solved!
    if (timerInterval) clearInterval(timerInterval);
    playVictoryChime();
    window.spawnConfettiCelebration();
    openVictoryModal(p);
  }

  // Tool Buttons
  document.getElementById('btn-tool-undo')?.addEventListener('click', () => {
    if (moveHistory.length === 0) {
      setStatusMessage('পূর্বাবস্থায় ফেরার মতো কোনো চাল নেই।');
      return;
    }
    const last = moveHistory.pop();
    currentBoard[last.r][last.c] = last.prevVal;
    selectedRC = [last.r, last.c];
    playTone(420, 'sine', 0.1);
    renderSudokuBoard();
    renderBengaliKeypad();
  });

  document.getElementById('btn-tool-erase')?.addEventListener('click', () => {
    const [r, c] = selectedRC;
    if (givenMask[r][c]) return;
    currentBoard[r][c] = 0;
    notesBoard[r][c].clear();
    playTone(380, 'sine', 0.1);
    renderSudokuBoard();
    renderBengaliKeypad();
  });

  document.getElementById('btn-tool-note')?.addEventListener('click', () => {
    isNoteMode = !isNoteMode;
    updateHud();
    playTone(520, 'sine', 0.1);
    setStatusMessage(isNoteMode ? '✏️ টীকা (Notes) মোড চালু হয়েছে।' : '✏️ টীকা মোড বন্ধ করা হয়েছে।');
  });

  document.getElementById('btn-tool-hint')?.addEventListener('click', () => {
    const p = PUZZLE_DATA[activeModeKey] || PUZZLE_DATA['word_secret'];
    if (hintsLeft <= 0) {
      setStatusMessage('আপনার সব ইঙ্গিত (Hints) শেষ হয়ে গেছে!', true);
      return;
    }
    let [r, c] = selectedRC;
    if (givenMask[r][c] || currentBoard[r][c] === p.solution[r][c]) {
      // Find any unsolved cell
      outer: for (let rr = 0; rr < p.size; rr++) {
        for (let cc = 0; cc < p.size; cc++) {
          if (currentBoard[rr][cc] !== p.solution[rr][cc]) {
            r = rr;
            c = cc;
            break outer;
          }
        }
      }
    }
    const correctVal = p.solution[r][c];
    currentBoard[r][c] = correctVal;
    selectedRC = [r, c];
    hintsLeft--;
    playTone(840, 'triangle', 0.2);
    setStatusMessage(`💡 ইঙ্গিত: (${toBnNum(r + 1)}, ${toBnNum(c + 1)}) ঘরে "${p.letters[correctVal - 1]}" বসানো হয়েছে!`);
    updateHud();
    const conflicts = findConflicts(currentBoard, p.size, p.blockRows, p.blockCols);
    renderSudokuBoard();
    renderBengaliKeypad();
    checkVictory(p, conflicts);
  });

  // Instant Solve Celebration Button
  document.getElementById('btn-instant-solve')?.addEventListener('click', () => {
    const p = PUZZLE_DATA[activeModeKey] || PUZZLE_DATA['word_secret'];
    currentBoard = p.solution.map((row) => [...row]);
    renderSudokuBoard();
    renderBengaliKeypad();
    if (timerInterval) clearInterval(timerInterval);
    playVictoryChime();
    window.spawnConfettiCelebration();
    openVictoryModal(p);
  });

  document.getElementById('btn-reset-board')?.addEventListener('click', () => {
    startPuzzleMode(activeModeKey);
    playTone(480, 'sine', 0.12);
  });

  // Mode Tabs in Playground
  document.querySelectorAll('.play-tab-btn').forEach((btn) => {
    btn.addEventListener('click', () => {
      const m = btn.getAttribute('data-play-mode');
      startPuzzleMode(m);
      playTone(580, 'triangle', 0.12);
    });
  });

  // "Try This Mode" Buttons on the 5 Game Mode Cards
  document.querySelectorAll('[data-try-mode]').forEach((btn) => {
    btn.addEventListener('click', (e) => {
      e.stopPropagation();
      const m = btn.getAttribute('data-try-mode');
      document.getElementById('play-section')?.scrollIntoView({ behavior: 'smooth' });
      setTimeout(() => startPuzzleMode(m), 350);
    });
  });

  // ==========================================================================
  // 5. WORD HUNT (শব্দ সন্ধান) INTERACTIVE WHEEL ENGINE
  // ==========================================================================
  const WORD_HUNT_DATA = {
    letters: ['ক', 'ল', 'ম', 'ব', 'র'],
    words: [
      { word: 'কলম', meaning: 'লেখার সরঞ্জাম (Pen)', found: false },
      { word: 'কমল', meaning: 'পদ্ম ফুল (Lotus)', found: false },
      { word: 'বর', meaning: 'স্বামী বা আশীর্বাদ (Groom)', found: false },
      { word: 'বল', meaning: 'খেলার গোলক বা শক্তি (Ball/Power)', found: false },
      { word: 'মর', meaning: 'মরণশীল বা নশ্বর', found: false },
      { word: 'কল', meaning: 'যন্ত্র বা পানির ট্যাপ', found: false }
    ]
  };

  let composedWord = '';
  const wheelEl = document.getElementById('word-wheel-circle');
  const composedDisplayEl = document.getElementById('composed-word-display');
  const wordTargetsEl = document.getElementById('word-targets-list');
  const wordHuntMsgEl = document.getElementById('word-hunt-feedback');

  function initWordHuntGame() {
    composedWord = '';
    renderWordWheel();
    renderWordTargets();
    updateComposedDisplay();
  }

  function renderWordWheel() {
    if (!wheelEl) return;
    wheelEl.innerHTML = '';
    const letters = WORD_HUNT_DATA.letters;
    const radius = 92;
    const center = 135;

    letters.forEach((ltr, idx) => {
      const angle = (idx * 2 * Math.PI) / letters.length - Math.PI / 2;
      const x = center + radius * Math.cos(angle);
      const y = center + radius * Math.sin(angle);

      const node = document.createElement('button');
      node.type = 'button';
      node.className = 'wheel-letter-node';
      node.style.left = `${x}px`;
      node.style.top = `${y}px`;
      node.textContent = ltr;

      node.addEventListener('click', () => {
        composedWord += ltr;
        node.classList.add('selected');
        playTone(520 + composedWord.length * 60, 'triangle', 0.12);
        updateComposedDisplay();
      });

      wheelEl.appendChild(node);
    });
  }

  function renderWordTargets() {
    if (!wordTargetsEl) return;
    wordTargetsEl.innerHTML = '';
    WORD_HUNT_DATA.words.forEach((item) => {
      const div = document.createElement('div');
      div.className = `word-target-chip ${item.found ? 'found' : ''}`;
      div.innerHTML = `
        <div style="display:flex;justify-content:space-between;align-items:center;">
          <strong style="font-size:1.1rem;color:var(--accent-secondary);">
            ${item.found ? item.word : '• '.repeat(item.word.length)}
          </strong>
          <span>${item.found ? '✅' : '🔒'}</span>
        </div>
        <span style="font-size:0.8rem;color:var(--text-secondary);">
          ${item.found ? item.meaning : `${toBnNum(item.word.length)} বর্ণের বাংলা শব্দ`}
        </span>
      `;
      wordTargetsEl.appendChild(div);
    });
  }

  function updateComposedDisplay() {
    if (composedDisplayEl) {
      composedDisplayEl.textContent = composedWord || 'বর্ণে ট্যাপ করে শব্দ তৈরি করুন';
    }
  }

  document.getElementById('btn-wh-submit')?.addEventListener('click', () => {
    const match = WORD_HUNT_DATA.words.find((w) => w.word === composedWord);
    if (match && !match.found) {
      match.found = true;
      playVictoryChime();
      window.spawnConfettiCelebration();
      if (wordHuntMsgEl) {
        wordHuntMsgEl.textContent = `🎉 অভিনন্দন! "${match.word}" শব্দটি খুঁজে পেয়েছেন — অর্থ: ${match.meaning}`;
        wordHuntMsgEl.style.color = '#34d399';
      }
    } else if (match && match.found) {
      if (wordHuntMsgEl) {
        wordHuntMsgEl.textContent = `"${match.word}" শব্দটি ইতিমধ্যে খুঁজে পাওয়া হয়েছে!`;
        wordHuntMsgEl.style.color = '#fbbf24';
      }
    } else {
      playErrorBuzz();
      if (wordHuntMsgEl) {
        wordHuntMsgEl.textContent = `"${composedWord || '...'}" তালিকায় নেই। আবার চেষ্টা করুন (যেমন: কলম, কমল, বল, বর)!`;
        wordHuntMsgEl.style.color = '#fca5a5';
      }
    }
    composedWord = '';
    document.querySelectorAll('.wheel-letter-node').forEach((n) => n.classList.remove('selected'));
    updateComposedDisplay();
    renderWordTargets();
  });

  document.getElementById('btn-wh-clear')?.addEventListener('click', () => {
    composedWord = '';
    document.querySelectorAll('.wheel-letter-node').forEach((n) => n.classList.remove('selected'));
    updateComposedDisplay();
    playTone(360, 'sine', 0.08);
  });

  // ==========================================================================
  // 6. GAMIFICATION: PLAYER RANK XP SIMULATOR (6 RANKS FROM PuzzleModels.kt)
  // ==========================================================================
  const PLAYER_RANKS = [
    { level: 1, titleBn: 'শিক্ষানবিস (Novice)', minXp: 0, maxXp: 300, icon: '🌱', desc: 'বাংলা বর্ণ ধাঁধার জগতে আপনার যাত্রা শুরু!' },
    { level: 2, titleBn: 'বর্ণসন্ধানী (Letter Explorer)', minXp: 300, maxXp: 800, icon: '🧭', desc: '৪×৪ ও ৬×৬ গ্রিডে বর্ণের অবস্থান খুঁজে বের করার দক্ষতা।' },
    { level: 3, titleBn: 'ধাঁধা সাধক (Puzzle Adept)', minXp: 800, maxXp: 1600, icon: '🧩', desc: 'টীকা ও কৌশল ব্যবহার করে দ্রুত ধাঁধা সমাধানকারী।' },
    { level: 4, titleBn: 'সুডোকু পণ্ডিত (Sudoku Scholar)', minXp: 1600, maxXp: 3000, icon: '🎓', desc: 'নিখুঁত ও ভুলহীনভাবে শব্দ সুডোকু জয়ের অভিজ্ঞতা।' },
    { level: 5, titleBn: 'শব্দ জাদুকর (Word Wizard)', minXp: 3000, maxXp: 5000, icon: '✨', desc: 'গোপন বাংলা শব্দ ও ৯×৯ গ্রিডের জটিল কৌশলে পারদর্শী।' },
    { level: 6, titleBn: 'বর্ণসম্রাট (Wordoku Emperor)', minXp: 5000, maxXp: 10000, icon: '👑', desc: 'বর্ণ-সুডোকুর সর্বোচ্চ মুকুটধারী অপরাজেয় সম্রাট!' }
  ];

  const xpSlider = document.getElementById('xp-simulator-slider');
  const rankTitleDisplay = document.getElementById('sim-rank-title');
  const rankLevelDisplay = document.getElementById('sim-rank-level');
  const rankDescDisplay = document.getElementById('sim-rank-desc');
  const rankIconDisplay = document.getElementById('sim-rank-icon');
  const xpValueDisplay = document.getElementById('sim-xp-value');
  const rankStepPills = document.querySelectorAll('[data-rank-level]');

  function updateRankSimulator(xpVal) {
    const xp = Number(xpVal);
    let currentRank = PLAYER_RANKS[0];
    for (const r of PLAYER_RANKS) {
      if (xp >= r.minXp) currentRank = r;
    }
    if (rankTitleDisplay) rankTitleDisplay.textContent = currentRank.titleBn;
    if (rankLevelDisplay) rankLevelDisplay.textContent = `লেভেল ${toBnNum(currentRank.level)} পদবী`;
    if (rankDescDisplay) rankDescDisplay.textContent = currentRank.desc;
    if (rankIconDisplay) rankIconDisplay.textContent = currentRank.icon;
    if (xpValueDisplay) xpValueDisplay.textContent = `${toBnNum(xp)} XP`;

    rankStepPills.forEach((pill) => {
      const lvl = Number(pill.getAttribute('data-rank-level'));
      pill.classList.toggle('active', lvl === currentRank.level);
    });
  }

  xpSlider?.addEventListener('input', (e) => {
    updateRankSimulator(e.target.value);
  });

  rankStepPills.forEach((pill) => {
    pill.addEventListener('click', () => {
      const targetXp = Number(pill.getAttribute('data-rank-xp') || 0);
      if (xpSlider) xpSlider.value = String(targetXp);
      updateRankSimulator(targetXp);
      playTone(500 + targetXp * 0.08, 'triangle', 0.14);
    });
  });

  // ==========================================================================
  // 7. MODALS (VICTORY DIALOG, RULES DIALOG, APK DOWNLOAD & SHARE)
  // ==========================================================================
  const rulesModal = document.getElementById('rules-modal');
  const victoryModal = document.getElementById('victory-modal');
  const downloadModal = document.getElementById('download-modal');

  function openVictoryModal(p) {
    if (!victoryModal) return;
    const basePts = p.size === 4 ? 100 : (p.secretWord ? 320 : 250);
    const speedBonus = Math.max(20, 140 - elapsedSec);
    const accBonus = mistakes === 0 ? 100 : 50;
    const wordBonus = p.secretWord ? 80 : 0;
    const total = basePts + speedBonus + accBonus + wordBonus;

    document.getElementById('vic-puzzle-name').textContent = p.titleBn;
    document.getElementById('vic-time').textContent = formatBnTime(elapsedSec);
    document.getElementById('vic-total-pts').textContent = `+${toBnNum(total)} XP`;

    const secretBox = document.getElementById('vic-secret-word-box');
    if (secretBox) {
      if (p.secretWord) {
        secretBox.style.display = 'block';
        document.getElementById('vic-secret-word').textContent = p.secretWord;
        document.getElementById('vic-secret-meaning').textContent = p.secretWordMeaning;
      } else {
        secretBox.style.display = 'none';
      }
    }
    victoryModal.classList.add('open');
  }

  document.querySelectorAll('[data-open-rules]').forEach((btn) => {
    btn.addEventListener('click', () => {
      rulesModal?.classList.add('open');
      playTone(540, 'sine', 0.1);
    });
  });

  document.querySelectorAll('[data-close-modal]').forEach((btn) => {
    btn.addEventListener('click', () => {
      rulesModal?.classList.remove('open');
      victoryModal?.classList.remove('open');
      downloadModal?.classList.remove('open');
    });
  });

  // APK Download Trigger with Celebration Toast Modal
  document.querySelectorAll('[data-trigger-apk-download]').forEach((btn) => {
    btn.addEventListener('click', () => {
      playVictoryChime();
      window.spawnConfettiCelebration();
      downloadModal?.classList.add('open');
    });
  });

  // Copy CLI or Challenge Share Text
  document.getElementById('btn-share-challenge')?.addEventListener('click', () => {
    const shareText = `🎮 বর্ণ-সুডোকু চ্যালেঞ্জ!\nআমার বর্তমান পদবী: সুডোকু পণ্ডিত (Sudoku Scholar)\n🏅 অর্জিত পদক: ৬ টি\n⭐ মোট পয়েন্ট: ২,৪৫০ XP\n\nতুমি কি বাংলা বর্ণ ও শব্দের এই সুডোকু ধাঁধায় পারদর্শী? এখনই অ্যাপটি ডাউনলোড করে খেলে দেখ!`;
    navigator.clipboard?.writeText(shareText);
    const btn = document.getElementById('btn-share-challenge');
    if (btn) {
      const orig = btn.innerHTML;
      btn.innerHTML = '✅ চ্যালেঞ্জ বার্তা কপি হয়েছে!';
      setTimeout(() => { btn.innerHTML = orig; }, 2500);
    }
    playTone(740, 'triangle', 0.16);
  });

  // Theme & Sound Toggles
  const themeBtn = document.getElementById('btn-theme-toggle');
  themeBtn?.addEventListener('click', () => {
    const current = document.documentElement.getAttribute('data-theme');
    const next = current === 'light' ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', next);
    themeBtn.textContent = next === 'light' ? '🌙' : '☀️';
    playTone(600, 'sine', 0.1);
  });

  const soundBtn = document.getElementById('btn-sound-toggle');
  soundBtn?.addEventListener('click', () => {
    soundEnabled = !soundEnabled;
    soundBtn.textContent = soundEnabled ? '🔊' : '🔇';
    if (soundEnabled) playTone(660, 'triangle', 0.12);
  });

  // ==========================================================================
  // 8. DYNAMIC QR CODE GENERATOR FOR APK DOWNLOAD
  // ==========================================================================
  function drawStylizedQrCode() {
    const qrCanvas = document.getElementById('qr-canvas');
    if (!qrCanvas) return;
    const qctx = qrCanvas.getContext('2d');
    const size = 160;
    const modules = 21;
    const cell = size / modules;

    qctx.fillStyle = '#ffffff';
    qctx.fillRect(0, 0, size, size);

    function drawFinder(ox, oy) {
      qctx.fillStyle = '#047857';
      qctx.fillRect(ox * cell, oy * cell, 7 * cell, 7 * cell);
      qctx.fillStyle = '#ffffff';
      qctx.fillRect((ox + 1) * cell, (oy + 1) * cell, 5 * cell, 5 * cell);
      qctx.fillStyle = '#0f172a';
      qctx.fillRect((ox + 2) * cell, (oy + 2) * cell, 3 * cell, 3 * cell);
    }

    drawFinder(0, 0);
    drawFinder(modules - 7, 0);
    drawFinder(0, modules - 7);

    // Deterministic pattern seeded from URL
    let seed = 1337;
    const urlStr = window.location.href + 'BornoSudoku-v1.0.0.apk';
    for (let i = 0; i < urlStr.length; i++) {
      seed = (seed * 31 + urlStr.charCodeAt(i)) % 1000003;
    }

    for (let r = 0; r < modules; r++) {
      for (let c = 0; c < modules; c++) {
        const inTL = r < 8 && c < 8;
        const inTR = r < 8 && c >= modules - 8;
        const inBL = r >= modules - 8 && c < 8;
        const inCenter = r >= 8 && r <= 12 && c >= 8 && c <= 12;
        if (inTL || inTR || inBL || inCenter) continue;

        seed = (seed * 16807) % 2147483647;
        if (seed % 10 < 5) {
          qctx.fillStyle = (r + c) % 3 === 0 ? '#047857' : '#0f172a';
          qctx.fillRect(c * cell + 0.5, r * cell + 0.5, cell - 1, cell - 1);
        }
      }
    }

    // Center Bengali Logo Badge 'ক'
    qctx.fillStyle = '#047857';
    drawRoundedRect(qctx, 8 * cell, 8 * cell, 5 * cell, 5 * cell, 6);
    qctx.fill();
    qctx.fillStyle = '#ffffff';
    qctx.font = '700 18px "Hind Siliguri Local", sans-serif';
    qctx.textAlign = 'center';
    qctx.textBaseline = 'middle';
    qctx.fillText('ক', size / 2, size / 2 + 1);
  }

  // Initialize Default Playable Game & QR
  startPuzzleMode('word_secret');
  updateRankSimulator(1850);
  drawStylizedQrCode();
})();
