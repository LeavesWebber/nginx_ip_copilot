<template>
    <div class="logo">
      <img src="../../images/xmut.jpg" alt="XMUT Logo" width="64" height="64" />
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
  
  img {
    border-radius: 50%;
    object-fit: cover;
  }
  </style>