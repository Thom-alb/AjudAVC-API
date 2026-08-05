import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Constants from 'expo-constants';

// Pega dinamicamente o IP da máquina onde o servidor/Expo está rodando
const hostUri = Constants.expoConfig?.hostUri?.split(':')[0];

// Se estiver no Expo Go / dispositivo físico na mesma rede Wi-Fi, usa o IP detectado.
// Se estiver no emulador Android do Android Studio, faz fallback para 10.0.2.2.
const localIp = hostUri ? hostUri : '10.0.2.2';

const API_URL = `http://${localIp}:8055`;

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