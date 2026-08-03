import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Constants from 'expo-constants';

// Pega o IP do Expo em dev ou usa 10.0.2.2 para Emulador Android
const localhost = Constants.expoConfig?.hostUri?.split(':')[0];
const API_URL = localhost ? `http://${localhost}:8055` : 'http://10.0.2.2:8055';

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