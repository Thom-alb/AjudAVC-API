import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
  SafeAreaView,
  StatusBar,
  ScrollView,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
import { GoogleSignin, statusCodes } from '@react-native-google-signin/google-signin';
import Estilos from '../Estilo/registro';
import api from '../src/service/api';

export default function RegisterScreen() {
  const router = useRouter();

  // Estados dos inputs
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [confirmEmail, setConfirmEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  // Estados de controle
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [termsAccepted, setTermsAccepted] = useState(false);
  const [loading, setLoading] = useState(false);
  const [googleLoading, setGoogleLoading] = useState(false);

  // Configuração inicial do Google Sign-In
  useEffect(() => {
    GoogleSignin.configure({
      webClientId: '.apps.googleusercontent.com', 
    });
  }, []);

  // Handler de Registro Tradicional
  const handleRegister = async () => {
    const cleanEmail = email.trim().toLowerCase();
    const cleanConfirmEmail = confirmEmail.trim().toLowerCase();

    if (!name.trim() || !cleanEmail || !password) {
      Alert.alert('Atenção', 'Preencha todos os campos obrigatórios.');
      return;
    }

    if (cleanEmail !== cleanConfirmEmail) {
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
      await api.post('/auth/register', {
        name: name.trim(),
        email: cleanEmail,
        password,
      });

      Alert.alert('Sucesso!', 'Conta criada com sucesso.', [
        { text: 'OK', onPress: () => router.replace('/login') },
      ]);
    } catch (error) {
      const menssagemErro =
        error.response?.data?.message || 'Erro ao realizar o cadastro.';
      Alert.alert('Erro no Cadastro', menssagemErro);
    } finally {
      setLoading(false);
    }
  };

  // Handler de Autenticação com Google
  const handleGoogleRegister = async () => {
    try {
      setGoogleLoading(true);
      await GoogleSignin.hasPlayServices();
      const userInfo = await GoogleSignin.signIn();
      const idToken = userInfo.data?.idToken ?? userInfo.idToken;

      if (!idToken) {
        Alert.alert('Erro', 'Não foi possível obter o token de validação do Google.');
        return;
      }

      // Envia o idToken para a rota /auth/google da sua API Spring Boot
      await api.post('/auth/google', { idToken });

      Alert.alert('Sucesso!', 'Autenticação via Google realizada com sucesso.');
      router.replace('/login');
    } catch (error) {
      if (error.code === statusCodes.SIGN_IN_CANCELLED) {
        // Usuário cancelou a janela do Google Sign-In
        return;
      }
      console.error(error);
      Alert.alert('Erro no Google Sign-In', 'Falha ao autenticar com a conta do Google.');
    } finally {
      setGoogleLoading(false);
    }
  };

  return (
    <SafeAreaView style={Estilos.container}>
      <StatusBar barStyle="light-content" backgroundColor="#73A5C6" />

      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={{ flex: 1 }}
      >
        <ScrollView
          contentContainerStyle={Estilos.scrollContent}
          keyboardShouldPersistTaps="handled"
          showsVerticalScrollIndicator={false}
        >
          <View style={Estilos.card}>
            {/* Botão de Voltar */}
            <TouchableOpacity
              style={Estilos.backButton}
              onPress={() => router.back()}
            >
              <Ionicons name="arrow-back" size={28} color="#FFFFFF" />
            </TouchableOpacity>

            <Text style={Estilos.title}>Crie sua conta</Text>

            {/* Input Nome */}
            <TextInput
              style={Estilos.input}
              placeholder="Nome"
              placeholderTextColor="#A0C1E5"
              value={name}
              onChangeText={setName}
            />

            {/* Input Email */}
            <TextInput
              style={Estilos.input}
              placeholder="Email"
              placeholderTextColor="#A0C1E5"
              keyboardType="email-address"
              autoCapitalize="none"
              value={email}
              onChangeText={setEmail}
            />

            {/* Input Confirmar Email */}
            <TextInput
              style={Estilos.input}
              placeholder="Confirmar Email"
              placeholderTextColor="#A0C1E5"
              keyboardType="email-address"
              autoCapitalize="none"
              value={confirmEmail}
              onChangeText={setConfirmEmail}
            />

            {/* Input Senha */}
            <View style={Estilos.passwordContainer}>
              <TextInput
                style={Estilos.passwordInput}
                placeholder="Senha"
                placeholderTextColor="#A0C1E5"
                secureTextEntry={!showPassword}
                value={password}
                onChangeText={setPassword}
              />
              <TouchableOpacity
                onPress={() => setShowPassword(!showPassword)}
                style={Estilos.eyeIcon}
              >
                <Ionicons
                  name={showPassword ? 'eye-off' : 'eye'}
                  size={22}
                  color="#A0C1E5"
                />
              </TouchableOpacity>
            </View>

            {/* Input Confirmar Senha */}
            <View style={Estilos.passwordContainer}>
              <TextInput
                style={Estilos.passwordInput}
                placeholder="Confirmar Senha"
                placeholderTextColor="#A0C1E5"
                secureTextEntry={!showConfirmPassword}
                value={confirmPassword}
                onChangeText={setConfirmPassword}
              />
              <TouchableOpacity
                onPress={() => setShowConfirmPassword(!showConfirmPassword)}
                style={Estilos.eyeIcon}
              >
                <Ionicons
                  name={showConfirmPassword ? 'eye-off' : 'eye'}
                  size={22}
                  color="#A0C1E5"
                />
              </TouchableOpacity>
            </View>

            {/* Checkbox de Termos */}
            <TouchableOpacity
              style={Estilos.checkboxContainer}
              onPress={() => setTermsAccepted(!termsAccepted)}
            >
              <View
                style={[
                  Estilos.checkbox,
                  termsAccepted && Estilos.checkboxChecked,
                ]}
              >
                {termsAccepted && (
                  <Ionicons name="checkmark" size={14} color="#FFFFFF" />
                )}
              </View>
              <Text style={Estilos.checkboxLabel}>
                Concordo com termos e condições
              </Text>
            </TouchableOpacity>

            {/* Botão de Registro Tradicional */}
            <TouchableOpacity
              style={Estilos.buttonPrimary}
              onPress={handleRegister}
              disabled={loading || googleLoading}
            >
              {loading ? (
                <ActivityIndicator color="#FFFFFF" />
              ) : (
                <Text style={Estilos.buttonText}>Registrar</Text>
              )}
            </TouchableOpacity>

            {/* Divisora "OU" */}
            <View style={Estilos.dividerContainer}>
              <View style={Estilos.dividerLine} />
              <Text style={Estilos.dividerText}>OU</Text>
              <View style={Estilos.dividerLine} />
            </View>

            {/* Botão de Registro/Login com Google */}
            <TouchableOpacity
              style={Estilos.googleButton}
              onPress={handleGoogleRegister}
              disabled={loading || googleLoading}
            >
              {googleLoading ? (
                <ActivityIndicator color="#4285F4" />
              ) : (
                <>
                  <Ionicons name="logo-google" size={20} color="#4285F4" style={{ marginRight: 10 }} />
                  <Text style={Estilos.googleButtonText}>Continuar com Google</Text>
                </>
              )}
            </TouchableOpacity>

            {/* Link para Ir ao Login */}
            <TouchableOpacity
              onPress={() => router.push('/login')}
              style={Estilos.linkContainer}
            >
              <Text style={Estilos.linkText}>Já tem conta? entre</Text>
            </TouchableOpacity>
          </View>

          {/* Botão Sair / Voltar ao Início */}
          <TouchableOpacity
            style={Estilos.exitButton}
            onPress={() => router.replace('/')}
          >
            <Text style={Estilos.exitButtonText}>Sair</Text>
          </TouchableOpacity>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}