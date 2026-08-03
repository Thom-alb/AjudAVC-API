import React, { useState } from 'react';
import { View, Text, TextInput, Button, StyleSheet, Alert, TouchableOpacity } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import api from '../services/api';

export default function LoginScreen({ navigation }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [isLogin, setIsLogin] = useState(true);

  const handleAuth = async () => {
    // Validação básica
    if (!email || !password || (!isLogin && !name)) {
      Alert.alert('Erro', 'Preencha todos os campos!');
      return;
    }

    try {
      if (isLogin) {
        // --- LOGIN ---
        const response = await api.post('/auth/login', { email, password });
        const { token } = response.data; 
        
        await AsyncStorage.setItem('authToken', token);
        
        console.log('Token salvo com sucesso');
        Alert.alert('Sucesso', 'Login realizado!');
        
        navigation.replace('Home');

      } else {
        // --- REGISTRO ---
        await api.post('/auth/register', { name, email, password });
        Alert.alert('Sucesso', 'Usuário criado! Faça login.');
        setIsLogin(true); 

        setName('');
        setEmail('');
        setPassword('');
      }
    } catch (error) {
      console.error(error);

      const msg = error.response?.data?.message || 'Erro na conexão com o servidor.';
      Alert.alert('Erro', msg);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>{isLogin ? 'Acessar Conta' : 'Criar Conta'}</Text>

      {!isLogin && (
        <TextInput 
          placeholder="Nome Completo" 
          style={styles.input} 
          value={name}
          onChangeText={setName}
        />
      )}

      <TextInput 
        placeholder="E-mail" 
        style={styles.input} 
        value={email}
        onChangeText={setEmail}
        keyboardType="email-address"
        autoCapitalize="none"
      />
      
      <TextInput 
        placeholder="Senha" 
        style={styles.input} 
        value={password}
        onChangeText={setPassword}
        secureTextEntry 
      />

      <Button title={isLogin ? 'Entrar' : 'Cadastrar'} onPress={handleAuth} />

      <TouchableOpacity onPress={() => setIsLogin(!isLogin)} style={{ marginTop: 20 }}>
        <Text style={{ color: '#6200ee', textAlign: 'center' }}>
          {isLogin ? 'Não tem conta? Cadastre-se' : 'Já tem conta? Faça Login'}
        </Text>
      </TouchableOpacity>
      
      <View style={{ marginTop: 30 }}>
        <Button title="Voltar para Início" color="#888" onPress={() => navigation.goBack()} />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', padding: 20, backgroundColor: '#f5f5f5' },
  title: { fontSize: 28, fontWeight: 'bold', marginBottom: 30, textAlign: 'center' },
  input: { backgroundColor: '#fff', padding: 15, borderRadius: 8, marginBottom: 15, borderWidth: 1, borderColor: '#ddd' },
});   