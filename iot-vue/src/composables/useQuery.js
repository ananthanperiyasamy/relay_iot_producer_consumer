// composables/useQuery.js
import { ref } from "vue";
import { queryValue } from "../api/query";

export function useQuery() {
  const result = ref(null);
  const loading = ref(false);
  const error = ref(null);

  const runQuery = async (type, params) => {
    loading.value = true;
    error.value = null;
    try {
      const res = await queryValue(type, params);
      result.value = res.data;
    } catch (e) {
      error.value = e.message;
    } finally {
      loading.value = false;
    }
  };

  return { result, loading, error, runQuery };
}
