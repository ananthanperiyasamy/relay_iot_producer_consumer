import axios from 'axios'

export const getMaxEvent = async (startDate, endDate, type, clusterId=1) => {
  const url = `http://localhost:8082/query/max?startDateTime=${startDate}&endDateTime=${endDate}&eventType=${type}&clusterId=${clusterId}`
  const response = await axios.get(url)
  return response.data
}
