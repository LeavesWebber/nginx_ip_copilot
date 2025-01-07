<template>
    <button 
      class="app-button" 
      :class="{ loading }"
      @click="handleClick"
    >
      <span class="button-content">
        <slot></slot>
      </span>
      <span v-if="loading" class="loader"></span>
      <div class="ripple" v-if="rippleStyle" :style="rippleStyle"></div>
    </button>
  </template>
  
  <script setup>
  // ... 保持原有的 script 部分不变
  </script>
  
  <style scoped>
  .app-button {
    position: relative;
    padding: 14px 28px;
    background: linear-gradient(45deg, var(--gradient-start), var(--gradient-end));
    color: white;
    border: none;
    border-radius: 12px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    overflow: hidden;
    transition: all 0.3s ease;
    transform: translateY(0);
    box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
  }
  
  .app-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(79, 70, 229, 0.4);
  }
  
  .app-button:active {
    transform: translateY(0);
  }
  
  .loading {
    cursor: not-allowed;
    opacity: 0.8;
  }
  
  .ripple {
    position: absolute;
    background: rgba(255, 255, 255, 0.4);
    border-radius: 50%;
    transform: scale(0);
    animation: ripple 0.8s cubic-bezier(0.4, 0, 0.2, 1);
    pointer-events: none;
  }
  
  .loader {
    width: 20px;
    height: 20px;
    border: 3px solid #fff;
    border-bottom-color: transparent;
    border-radius: 50%;
    display: inline-block;
    margin-left: 8px;
    animation: rotation 1s linear infinite;
  }
  
  @keyframes ripple {
    to {
      transform: scale(4);
      opacity: 0;
    }
  }
  
  @keyframes rotation {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }
  </style>