import axios from 'axios';

// Создание экземпляра Axios
const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080', // Замените на ваш базовый URL API
  withCredentials: true, // Включение передачи cookies или других credentials
  headers: {
    'Content-Type': 'application/json', // Общий заголовок для JSON запросов
  },
});

// Интерцепторы для добавления токенов авторизации, если нужно
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken'); // Или другой источник токена
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Интерцепторы для обработки ошибок
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      console.error('Unauthorized, redirecting to login...');
      // Логика выхода из системы или перенаправления
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
