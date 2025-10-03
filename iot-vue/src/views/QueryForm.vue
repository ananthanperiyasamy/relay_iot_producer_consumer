<template>
  <div class="query-container">
    <div class="query-box">
      <h2>Query Data</h2>

      <label>Start Date:</label>
      <input type="date" v-model="startDate" />

      <label>End Date:</label>
      <input type="date" v-model="endDate" />

      <label>Type:</label>
      <select v-model="type">
        <option value="temperature">Temperature</option>
        <option value="humidity">Humidity</option>
      </select>

      <label>Operation:</label>
      <select v-model="operation">
        <option value="max">Maximum</option>
        <option value="min">Minimum</option>
        <option value="average">Average</option>
        <option value="median">Median</option>
      </select>

      <button @click="submitQuery">Submit</button>

      <ResultDisplay v-if="result" :data="result" />
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useAuthStore } from '../store/auth'
import ResultDisplay from '../components/ResultDisplay.vue'

export default {
  components: { ResultDisplay },
  setup() {
    const startDate = ref('')
    const endDate = ref('')
    const type = ref('temperature')
    const operation = ref('max') // default operation
    const result = ref(null)

    const auth = useAuthStore()  // Get JWT token from store

    const submitQuery = async () => {
      if (!startDate.value || !endDate.value) {
        return alert('Please select dates')
      }

      if (!auth.token) {
        return alert('You are not authenticated')
      }

      try {
        // Declare variables at the top BEFORE using them
        const eventType = type.value.toUpperCase()  // HUMIDITY / TEMPERATURE
        const op = operation.value                  // min/max/average/median

        // Construct URL dynamically based on operation
        const url = `http://localhost:8082/query/${op}?startDateTime=${startDate.value}&endDateTime=${endDate.value}&eventType=${eventType}&clusterId=1`

        const response = await fetch(url, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${auth.token}`
          }
        })

        if (!response.ok) {
          const errorData = await response.json(); 
          throw new Error(`${response.status} ${errorData.message}`);
        }

        result.value = await response.json()
      } catch (err) {
        console.error(err)
        result.value = 'Error: ' + err.message
      }
    }


    return { startDate, endDate, type, operation, result, submitQuery }
  }
}
</script>

<style scoped>
.query-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #6b73ff, #000dff);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.query-box {
  background: #ffffff;
  padding: 40px 30px;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
  text-align: center;
}

.query-box h2 {
  margin-bottom: 25px;
  color: #333;
}

.query-box label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #333;
  text-align: left;
}

.query-box input,
.query-box select {
  width: 100%;
  padding: 12px 15px;
  margin-bottom: 20px;
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 16px;
  transition: border 0.3s;
  box-sizing: border-box;
}

.query-box input:focus,
.query-box select:focus {
  outline: none;
  border-color: #6b73ff;
  box-shadow: 0 0 5px rgba(107, 115, 255, 0.5);
}

.query-box button {
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

.query-box button:hover {
  background-color: #000dff;
}
</style>
