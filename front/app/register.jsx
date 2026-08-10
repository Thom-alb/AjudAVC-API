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
import { Ionicons } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
import api from '../src/service/api';

export default function RegisterScreen() {
  const router = useRouter();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [confirmEmail, setConfirmEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [termsAccepted, setTermsAccepted] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleRegister = async () => {
    // 1. Validações básicas
    if (!name || !email || !password) {
      Alert.alert('Atenção', 'Preencha todos os campos obrigatórios.');
      return;
    }

    if (email !== confirmEmail) {
      Alert.alert('Atenção', 'Os e-mails digitados não coincidem.');
      return;
    }

    if (password !== confirmPassword) {
      Alert.alert('Atenção', 'As senhas digitadas não coincidem.');
      return;
    }

    if (password.length < 6) {
      Alert.alert('Atenção', 'A senha deve ter no mínimo 6 caracteres.');
      return;
    }

    if (!termsAccepted) {
      Alert.alert('Atenção', 'Você deve aceitar os termos e condições.');
      return;
    }

    setLoading(true);

    try {
      // 2. Envia os dados para o endpoint /auth/register (UserRequestDTO)
      await api.post('/auth/register', {
        name,
        email,
        password,
      });

      Alert.alert('Sucesso!', 'Conta criada com sucesso.', [
        { text: 'OK', onPress: () => router.push('/login') },
      ]);
    } catch (error) {
      const menssagemErro =
        error.response?.data?.message || 'Erro ao realizar o cadastro.';
      Alert.alert('Erro no Cadastro', menssagemErro);
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="light-content" backgroundColor="#73A5C6" />

      <View style={styles.card}>
        {/* Botão de Voltar */}
        <TouchableOpacity
          style={styles.backButton}
          onPress={() => router.back()}
        >
          <Ionicons name="arrow-back" size={28} color="#FFFFFF" />
        </TouchableOpacity>

        <Text style={styles.title}>Crie sua conta</Text>

        <TextInput
          style={styles.input}
          placeholder="Nome"
          placeholderTextColor="#A0C1E5"
          value={name}
          onChangeText={setName}
        />

        <TextInput
          style={styles.input}
          placeholder="Email"
          placeholderTextColor="#A0C1E5"
          keyboardType="email-address"
          autoCapitalize="none"
          value={email}
          onChangeText={setEmail}
        />

        <TextInput
          style={styles.input}
          placeholder="Confirmar Email"
          placeholderTextColor="#A0C1E5"
          keyboardType="email-address"
          autoCapitalize="none"
          value={confirmEmail}
          onChangeText={setConfirmEmail}
        />

        <TextInput
          style={styles.input}
          placeholder="Senha"
          placeholderTextColor="#A0C1E5"
          secureTextEntry
          value={password}
          onChangeText={setPassword}
        />

        <TextInput
          style={styles.input}
          placeholder="Confirmar Senha"
          placeholderTextColor="#A0C1E5"
          secureTextEntry
          value={confirmPassword}
          onChangeText={setConfirmPassword}
        />

        {/* Checkbox Termos */}
        <TouchableOpacity
          style={styles.checkboxContainer}
          onPress={() => setTermsAccepted(!termsAccepted)}
        >
          <View style={[styles.checkbox, termsAccepted && styles.checkboxChecked]}>
            {termsAccepted && <Ionicons name="checkmark" size={14} color="#FFFFFF" />}
          </View>
          <Text style={styles.checkboxLabel}>
            Concordo com termos e condições
          </Text>
        </TouchableOpacity>

        {/* Botão Registrar */}
        <TouchableOpacity
          style={styles.buttonPrimary}
          onPress={handleRegister}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="#FFFFFF" />
          ) : (
            <Text style={styles.buttonText}>Registrar</Text>
          )}
        </TouchableOpacity>

        {/* Link para o Login */}
        <TouchableOpacity
          onPress={() => router.push('/login')}
          style={styles.linkContainer}
        >
          <Text style={styles.linkText}>Já tem conta? entre</Text>
        </TouchableOpacity>
      </View>

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
    backgroundColor: '#73A5C6',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  card: {
    width: '100%',
    backgroundColor: '#2E618E',
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
    marginBottom: 20,
  },
  input: {
    height: 44,
    borderBottomWidth: 1,
    borderBottomColor: '#6C9BCF',
    color: '#FFFFFF',
    fontSize: 15,
    marginBottom: 14,
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
    fontSize: 13,
  },
  buttonPrimary: {
    backgroundColor: '#6FA4E8',
    height: 48,
    borderRadius: 24,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 12,
    marginBottom: 12,
  },
  buttonText: {
    color: '#FFFFFF',
    fontSize: 18,
    fontWeight: 'bold',
  },
  linkContainer: {
    alignItems: 'center',
  },
  linkText: {
    color: '#E0E0E0',
    fontSize: 13,
  },
  exitButton: {
    marginTop: 20,
  },
  exitButtonText: {
    color: '#FFFFFF',
    fontSize: 16,
  },
});