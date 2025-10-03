import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLoggedIn: false,
    username: '',
    token: ''  // store JWT token
  }),
  actions: {
    /**
     * Perform login by calling backend API
     * @param {string} username 
     * @param {string} password 
     * @returns {boolean} true if login successful, false otherwise
     */
    async login(username, password) {
      if (!username || !password) return false

      try {
        const response = await fetch('http://localhost:8082/authenticate', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({  // <-- object, not array
            username,
            password
          })
        })

        if (!response.ok) {
          console.error('Login failed:', response.status)
          return false
        }

        const data = await response.json()
        // Assuming API returns { token: 'JWT_TOKEN_HERE' }
        if (data.jwttoken) {
          this.isLoggedIn = true
          this.username = username
          this.token = data.jwttoken
          return true
        }

        return false
      } catch (err) {
        console.error('Login error:', err)
        return false
      }
    },

    logout() {
      this.isLoggedIn = false
      this.username = ''
      this.token = ''
    },

    /**
     * Helper to perform authenticated API calls
     * @param {string} url 
     * @param {object} options 
     * @returns {Promise<Response>}
     */
    async authFetch(url, options = {}) {
      if (!this.token) throw new Error('Not authenticated')
      
      const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${this.token}`,
        ...options.headers
      }

      const response = await fetch(url, {
        ...options,
        headers
      })

      return response
    }
  }
})
