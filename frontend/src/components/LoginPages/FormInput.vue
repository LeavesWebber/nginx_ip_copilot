<template>
    <div class="input-group" :class="{ 'has-error': error }">
      <input
        :id="id"
        :type="type"
        :value="modelValue"
        @input="$emit('update:modelValue', $event.target.value)"
        :maxlength="type === 'password' ? 32 : 64"
        autocomplete="off"
        autofocus
        required
      >
      <label :for="id">{{ label }}</label>
      <span class="highlight"></span>
      <span v-if="error" class="error-message">{{ error }}</span>
    </div>
  </template>
  
  <script setup>
  defineProps({
    id: String,
    label: String,
    modelValue: String,
    type: String,
    error: String
  })
  
  defineEmits(['update:modelValue'])
  </script>
  
  <style scoped>
  .input-group {
    position: relative;
    margin-bottom: 1.5rem;
  }
  
  input {
    width: 100%;
    padding: 12px 16px;
    font-size: 1rem;
    border: 2px solid var(--input-border);
    border-radius: 12px;
    outline: none;
    background: rgba(255, 255, 255, 0.8);
    transition: all 0.3s ease;
    color: var(--text-color);
  }
  
  input[type="password"] {
    letter-spacing: 0.3em;
    font-family: monospace;
  }
  
  input:focus {
    border-color: var(--primary-color);
    background: rgba(255, 255, 255, 0.95);
    box-shadow: 0 0 0 4px rgba(79, 70, 229, 0.1);
  }
  
  label {
    position: absolute;
    left: 16px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 1rem;
    color: #6b7280;
    pointer-events: none;
    transition: all 0.3s ease;
    padding: 0 4px;
    background: transparent;
  }
  
  input:focus ~ label,
  input:valid ~ label {
    top: 0;
    font-size: 0.85rem;
    color: var(--primary-color);
    background: var(--card-background);
  }
  
  .has-error input {
    border-color: var(--error-color);
  }
  
  .error-message {
    color: var(--error-color);
    font-size: 0.85rem;
    margin-top: 6px;
    display: block;
    padding-left: 16px;
    opacity: 0;
    animation: fadeIn 0.3s ease forwards;
  }
  
  @keyframes fadeIn {
    to {
      opacity: 1;
    }
  }
  </style>