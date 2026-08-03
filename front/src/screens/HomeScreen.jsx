import React, { useEffect, useState } from 'react';
import { View, Text, FlatList, StyleSheet, Button, ActivityIndicator } from 'react-native';
import api from '../services/api';

export default function HomeScreen({ navigation, route }) {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Carregar usuários ao abrir a tela
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      // Chama GET /user do seu UserController
      const response = await api.get('/user');
      setUsers(response.data); // Seu backend retorna List<UserResponseDTO>
    } catch (error) {
      console.error('Erro ao carregar usuários:', error);
      Alert.alert('Erro', 'Não foi possível carregar a lista de usuários.');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    // Limpar token e voltar para login
    navigation.replace('Login');
  };

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#6200ee" />
        <Text>Carregando usuários...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Usuários Cadastrados</Text>
        <Button title="Sair" color="#d9534f" onPress={handleLogout} />
      </View>

      <FlatList
        data={users}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <View style={styles.card}>
            <Text style={styles.userName}>{item.name}</Text>
            <Text style={styles.userEmail}>{item.email}</Text>
          </View>
        )}
      />
      
      <View style={{ marginTop: 20 }}>
         <Button title="Recarregar Lista" onPress={loadUsers} />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, backgroundColor: '#f5f5f5' },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20, marginTop: 40 },
  title: { fontSize: 24, fontWeight: 'bold' },
  center: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  card: { backgroundColor: '#fff', padding: 15, borderRadius: 8, marginBottom: 10, elevation: 2 },
  userName: { fontSize: 18, fontWeight: 'bold' },
  userEmail: { color: '#666', marginTop: 5 },
});   