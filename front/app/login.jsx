import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
  ActivityIndicator,
  SafeAreaView,
  StatusBar,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Ionicons } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
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
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="light-content" backgroundColor="#73A5C6" />

      {/* Card Azul Escuro */}
      <View style={styles.card}>
        {/* Seta Voltar */}
        <TouchableOpacity
          style={styles.backButton}
          onPress={() => router.back()}
        >
          <Ionicons name="arrow-back" size={28} color="#FFFFFF" />
        </TouchableOpacity>

        {/* Título */}
        <Text style={styles.title}>Bem vindo(a)</Text>

        {/* Campo Email */}
        <TextInput
          style={styles.input}
          placeholder="Email"
          placeholderTextColor="#A0C1E5"
          keyboardType="email-address"
          autoCapitalize="none"
          value={email}
          onChangeText={setEmail}
        />

        {/* Campo Senha */}
        <TextInput
          style={styles.input}
          placeholder="Senha"
          placeholderTextColor="#A0C1E5"
          secureTextEntry
          value={password}
          onChangeText={setPassword}
        />

        {/* Checkbox Lembrar Login */}
        <TouchableOpacity
          style={styles.checkboxContainer}
          onPress={() => setRememberLogin(!rememberLogin)}
        >
          <View style={[styles.checkbox, rememberLogin && styles.checkboxChecked]}>
            {rememberLogin && <Ionicons name="checkmark" size={14} color="#FFFFFF" />}
          </View>
          <Text style={styles.checkboxLabel}>Lembrar Login</Text>
        </TouchableOpacity>

        {/* Botão Entrar */}
        <TouchableOpacity
          style={styles.buttonPrimary}
          onPress={handleLogin}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="#FFFFFF" />
          ) : (
            <Text style={styles.buttonText}>Entrar</Text>
          )}
        </TouchableOpacity>

        {/* Esqueceu a senha */}
        <TouchableOpacity
          style={styles.forgotContainer}
          onPress={() => Alert.alert('Recuperação', 'Recurso em desenvolvimento.')}
        >
          <Text style={styles.forgotText}>Esqueceu a senha?</Text>
        </TouchableOpacity>
      </View>

      {/* Botão Sair no rodapé */}
      <TouchableOpacity
        style={styles.exitButton}
        onPress={() => router.replace('/')}
      >
        <Text style={styles.exitButtonText}>Sair</Text>
      </TouchableOpacity>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#73A5C6', // Azul do fundo principal
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  card: {
    width: '100%',
    backgroundColor: '#2E618E', // Azul do card
    borderRadius: 24,
    padding: 24,
    alignItems: 'stretch',
  },
  backButton: {
    marginBottom: 8,
    alignSelf: 'flex-start',
  },
  title: {
    fontSize: 22,
    fontWeight: '600',
    color: '#FFFFFF',
    textAlign: 'center',
    marginBottom: 32,
  },
  input: {
    height: 48,
    borderBottomWidth: 1,
    borderBottomColor: '#6C9BCF',
    color: '#FFFFFF',
    fontSize: 16,
    marginBottom: 20,
    paddingHorizontal: 4,
  },
  checkboxContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: 12,
  },
  checkbox: {
    width: 18,
    height: 18,
    borderWidth: 1,
    borderColor: '#FFFFFF',
    marginRight: 10,
    justifyContent: 'center',
    alignItems: 'center',
  },
  checkboxChecked: {
    backgroundColor: '#6FA4E8',
    borderColor: '#6FA4E8',
  },
  checkboxLabel: {
    color: '#FFFFFF',
    fontSize: 14,
  },
  buttonPrimary: {
    backgroundColor: '#6FA4E8',
    height: 48,
    borderRadius: 24,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 20,
    marginBottom: 16,
  },
  buttonText: {
    color: '#FFFFFF',
    fontSize: 18,
    fontWeight: 'bold',
  },
  forgotContainer: {
    alignItems: 'flex-start',
    marginTop: 4,
  },
  forgotText: {
    color: '#E0E0E0',
    fontSize: 13,
  },
  exitButton: {
    marginTop: 32,
  },
  exitButtonText: {
    color: '#FFFFFF',
    fontSize: 16,
  },
});