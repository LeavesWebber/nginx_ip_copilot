<template>
    <div class="logo">
      <svg viewBox="0 0 24 24" width="64" height="64">
        <path fill="url(#logoGradient)" d="M12,3L1,9L12,15L21,10.09V17H23V9M5,13.18V17.18L12,21L19,17.18V13.18L12,17L5,13.18Z"/>
        <defs>
          <linearGradient id="logoGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" style="stop-color: var(--gradient-start)"/>
            <stop offset="100%" style="stop-color: var(--gradient-end)"/>
          </linearGradient>
        </defs>
      </svg>
    </div>
  </template>
  
  <style scoped>
  .logo {
    text-align: center;
    margin-bottom: 1.5rem;
    animation: float 6s ease-in-out infinite;
  }
  
  @keyframes float {
    0%, 100% {
      transform: translateY(0);
    }
    50% {
      transform: translateY(-10px);
    }
  }
  </style>