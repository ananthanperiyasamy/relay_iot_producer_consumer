<template>
    <div class="login-container">
      <div class="login-box">
        <h2>Login</h2>
        <input v-model="username" placeholder="Username" />
        <input v-model="password" type="password" placeholder="Password" />
        <button @click="handleLogin">Login</button>
        <p v-if="error" class="error">{{ error }}</p>
      </div>
    </div>
  </template>
  
  <script>
  import { ref } from 'vue'
  import { useAuthStore } from '../store/auth'
  import { useRouter } from 'vue-router'
  
  export default {
    setup() {
      const username = ref('')
      const password = ref('')
      const error = ref('')
      const auth = useAuthStore()
      const router = useRouter()
  
      const handleLogin = () => {
        if(auth.login(username.value, password.value)) {
          router.push('/query')
        } else {
          error.value = 'Invalid credentials'
        }
      }
  
      return { username, password, error, handleLogin }
    }
  }
  </script>
  
  <style scoped>
  /* Center the login box on the page */
  .login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background: linear-gradient(135deg, #6b73ff, #000dff);
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  }
  
  /* Styling the login box */
  .login-box {
    background: #ffffff;
    padding: 40px 30px;
    border-radius: 12px;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
    width: 100%;
    max-width: 360px;
    text-align: center;
  }
  
  /* Heading */
  .login-box h2 {
    margin-bottom: 25px;
    color: #333;
  }
  
  /* Inputs */
  .login-box input {
  width: 100%;
  padding: 12px 15px;
  margin-bottom: 15px;
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 16px;
  transition: border 0.3s;
  box-sizing: border-box; /* <-- add this */
}

  
  .login-box input:focus {
    outline: none;
    border-color: #6b73ff;
    box-shadow: 0 0 5px rgba(107, 115, 255, 0.5);
  }
  
  /* Button */
  .login-box button {
    width: 100%;
    padding: 12px;
    border: none;
    border-radius: 8px;
    background-color: #6b73ff;
    color: white;
    font-size: 16px;
    cursor: pointer;
    transition: background 0.3s;
  }
  
  .login-box button:hover {
    background-color: #000dff;
  }
  
  /* Error message */
  .error {
    margin-top: 10px;
    color: red;
    font-weight: 500;
    font-size: 14px;
  }
  </style>
  