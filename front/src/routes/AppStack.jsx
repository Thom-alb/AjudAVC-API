import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import HomeScreen from '../screens/HomeScreen';
import LoginScreen from '../screens/LoginScreen';

const Stack = createNativeStackNavigator();

export default function AppStack() {
  return (
    <NavigationContainer>
      <Stack.Navigator 
        initialRouteName="Login" // Começa no Login
        screenOptions={{
          headerStyle: { backgroundColor: '#6200ee' },
          headerTintColor: '#fff',
        }}
      >
        <Stack.Screen name="Login" component={LoginScreen} options={{ title: 'AjudAVC - Acesso' }} />
        <Stack.Screen name="Home" component={HomeScreen} options={{ title: 'Painel de Usuários' }} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}   