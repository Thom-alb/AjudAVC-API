import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
  SafeAreaView,
  StatusBar,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Ionicons } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
import Estilos from "../Estilo/login";
import api from '../src/service/api';

export default function LoginScreen() {
  const router = useRouter();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [rememberLogin, setRememberLogin] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleLogin = async () => {
    if (!email || !password) {
      Alert.alert('Atenção', 'Por favor, preencha o e-mail e a senha.');
      return;
    }

    setLoading(true);

    try {
      // Envia requisição para o Spring Boot (AuthenticationDTO)
      const response = await api.post('/auth/login', {
        email,
        password,
      });

      // Extrai o JWT token (TokenResponseDTO)
      const { token } = response.data;
      await AsyncStorage.setItem('authToken', token);

      Alert.alert('Sucesso', 'Login realizado com sucesso!');
      
      // Redireciona para a tela inicial/dashboard
      router.replace('/');
    } catch (error) {
      const menssagemErro =
        error.response?.data?.message || 'E-mail ou senha inválidos.';
      Alert.alert('Erro no Login', menssagemErro);
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={Estilos.container}>
      <StatusBar barStyle="light-content" backgroundColor="#73A5C6" />

      {/* Card Azul Escuro */}
      <View style={Estilos.card}>
        {/* Seta Voltar */}
        <TouchableOpacity
          style={Estilos.backButton}
          onPress={() => router.back()}
        >
          <Ionicons name="arrow-back" size={28} color="#FFFFFF" />
        </TouchableOpacity>

        {/* Título */}
        <Text style={Estilos.title}>Bem vindo(a)</Text>

        {/* Campo Email */}
        <TextInput
          style={Estilos.input}
          placeholder="Email"
          placeholderTextColor="#A0C1E5"
          keyboardType="email-address"
          autoCapitalize="none"
          value={email}
          onChangeText={setEmail}
        />

        {/* Campo Senha */}
        <TextInput
          style={Estilos.input}
          placeholder="Senha"
          placeholderTextColor="#A0C1E5"
          secureTextEntry
          value={password}
          onChangeText={setPassword}
        />

        {/* Checkbox Lembrar Login */}
        <TouchableOpacity
          style={Estilos.checkboxContainer}
          onPress={() => setRememberLogin(!rememberLogin)}
        >
          <View style={[Estilos.checkbox, rememberLogin && Estilos.checkboxChecked]}>
            {rememberLogin && <Ionicons name="checkmark" size={14} color="#FFFFFF" />}
          </View>
          <Text style={Estilos.checkboxLabel}>Lembrar Login</Text>
        </TouchableOpacity>

        {/* Botão Entrar */}
        <TouchableOpacity
          style={Estilos.buttonPrimary}
          onPress={handleLogin}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="#FFFFFF" />
          ) : (
            <Text style={Estilos.buttonText}>Entrar</Text>
          )}
        </TouchableOpacity>

        {/* Esqueceu a senha */}
        <TouchableOpacity
          style={Estilos.forgotContainer}
          onPress={() => Alert.alert('Recuperação', 'Recurso em desenvolvimento.')}
        >
          <Text style={Estilos.forgotText}>Esqueceu a senha?</Text>
        </TouchableOpacity>
      </View>

      {/* Botão Sair no rodapé */}
      <TouchableOpacity
        style={Estilos.exitButton}
        onPress={() => router.replace('/')}
      >
        <Text style={Estilos.exitButtonText}>Sair</Text>
      </TouchableOpacity>
    </SafeAreaView>
  );
}
