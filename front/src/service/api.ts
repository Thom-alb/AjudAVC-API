import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

// AJUSTE A URL DE ACORDO COM SEU TESTE:
// 1. Emulador Android: Use 'http://10.0.2.2:8055' (aponta para o localhost do seu PC)
// 2. Celular Físico (Android/iOS): Use 'http://SEU_IP_LOCAL:8055' (ex: 'http://192.168.1.15:8055')
// 3. iOS Simulator: Use 'http://localhost:8055'
const API_URL = 'http://10.0.2.2:8055'; 

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  async (config) => {
    try {
      const token = await AsyncStorage.getItem('authToken');
      
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    } catch (error) {
      console.error('Erro ao recuperar token:', error);
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default api;   