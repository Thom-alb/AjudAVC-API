import { Stack } from 'expo-router';

export default function Layout() {
  return (
    <Stack
      screenOptions={{
        headerShown: false, // Esconde o cabeçalho padrão em todas as telas
      }}
    >
      <Stack.Screen name="index" />
      <Stack.Screen name="login" />
      <Stack.Screen name="register" />
      <Stack.Screen name="home" />
      <Stack.Screen name="ajuda" />
      <Stack.Screen name="createGroup" />
      <Stack.Screen name="groupRole" />
      <Stack.Screen name="info-ajudavc" />
      <Stack.Screen name="info-avc" />
    </Stack>
  );
}