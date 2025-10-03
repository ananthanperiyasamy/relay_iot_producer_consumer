// composables/useSensors.js
import { ref } from "vue";
import { getSensors } from "../api/sensors";

export function useSensors() {
  const sensors = ref([]);
  const loading = ref(false);
  const error = ref(null);

  const loadSensors = async () => {
    loading.value = true;
    error.value = null;
    try {
      const res = await getSensors();
      sensors.value = res.data;
    } catch (e) {
      error.value = e.message;
    } finally {
      loading.value = false;
    }
  };

  return { sensors, loading, error, loadSensors };
}
