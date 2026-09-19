import axios from 'axios'

const http = axios.create({ baseURL: '/api', timeout: 10000 })

http.interceptors.response.use(
  (res) => res.data,
  (err) => Promise.reject(new Error(err?.response?.data?.message || err.message || '接口没通'))
)

export const enclosureApi = {
  list: (params) => http.get('/enclosures', { params }),
  occupancy: () => http.get('/enclosures/occupancy'),
  add: (b) => http.post('/enclosures', b),
  save: (id, b) => http.put(`/enclosures/${id}`, b)
}
export const animalApi = {
  list: (params) => http.get('/animals', { params }),
  add: (b) => http.post('/animals', b),
  save: (id, b) => http.put(`/animals/${id}`, b)
}
export const feedingApi = {
  list: (params) => http.get('/feedings', { params }),
  byKeeper: () => http.get('/feedings/by-keeper'),
  add: (b) => http.post('/feedings', b)
}
export const checkApi = {
  list: (params) => http.get('/checks', { params }),
  overview: () => http.get('/checks/overview'),
  add: (b) => http.post('/checks', b)
}
export const isolationMealApi = {
  list: () => http.get('/isolation-meals'),
  add: (b) => http.post('/isolation-meals', b)
}

export default http
