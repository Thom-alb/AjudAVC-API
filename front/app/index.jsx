import React from 'react';
import { 
  StyleSheet, 
  View, 
  Text, 
  TouchableOpacity, 
  Image, 
  BackHandler, 
  Platform 
} from 'react-native';
import { useRouter } from 'expo-router';

export default function WelcomeScreen() {
  const router = useRouter();

  // Função para fechar/sair da aplicação no Android
  const handleExitApp = () => {
    if (Platform.OS === 'android') {
      BackHandler.exitApp();
    } else {
      console.log('Botão sair clicado');
    }
  };

  return (
    <View style={styles.container}>
      {/* Container da Logo (Card Escuro) */}
      <View style={styles.logoCard}>
        <Image 
          source={require('../assets/images/logo.png')} 
          style={styles.logo}
          resizeMode="contain"
        />
      </View>

      {/* Seção de Botões Principais */}
      <View style={styles.buttonContainer}>
        <TouchableOpacity 
          style={styles.primaryButton}
          activeOpacity={0.8}
          onPress={() => router.push('/login')}
        >
          <Text style={styles.primaryButtonText}>Entrar</Text>
        </TouchableOpacity>

        <TouchableOpacity 
          style={styles.primaryButton}
          activeOpacity={0.8}
          onPress={() => router.push('/register')}
        >
          <Text style={styles.primaryButtonText}>Registrar</Text>
        </TouchableOpacity>

        {/* Link para Ajuda */}
        <TouchableOpacity 
          style={styles.helpButton}
          onPress={() => router.push('/ajuda')}
        >
          <Text style={styles.helpButtonText}>Ajuda</Text>
        </TouchableOpacity>
      </View>

      {/* Seção Saiba mais */}
      <View style={styles.infoSection}>
        <Text style={styles.infoTitle}>Saiba mais:</Text>
        <View style={styles.linksRow}>
          <TouchableOpacity onPress={() => router.push('/info-avc')}>
            <Text style={styles.linkText}>AVC</Text>
          </TouchableOpacity>
          
          <TouchableOpacity onPress={() => router.push('/info-ajudavc')}>
            <Text style={styles.linkText}>AjudAVC</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* Botão Sair */}
      <TouchableOpacity 
        style={styles.exitButton}
        onPress={handleExitApp}
      >
        <Text style={styles.exitButtonText}>Sair</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#72A3CE', // Cor de fundo azul claro
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 40,
    paddingHorizontal: 24,
  },
  logoCard: {
    width: '90%',
    height: '38%',
    backgroundColor: '#1E4969', // Card azul escuro
    borderRadius: 32,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 20,
    elevation: 5, // Sombra no Android
    shadowColor: '#000', // Sombra no iOS
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.15,
    shadowRadius: 8,
  },
  logo: {
    width: '80%',
    height: '80%',
  },
  buttonContainer: {
    width: '100%',
    alignItems: 'center',
    gap: 12,
  },
  primaryButton: {
    width: '90%',
    backgroundColor: '#2C6485', // Azul médio dos botões
    borderRadius: 25,
    paddingVertical: 14,
    alignItems: 'center',
  },
  primaryButtonText: {
    color: '#FFFFFF',
    fontSize: 20,
    fontWeight: 'bold',
  },
  helpButton: {
    marginTop: 4,
    padding: 8,
  },
  helpButtonText: {
    color: '#FFFFFF',
    fontSize: 18,
    fontWeight: '500',
  },
  infoSection: {
    alignItems: 'center',
    gap: 6,
  },
  infoTitle: {
    color: '#EAF3FA',
    fontSize: 18,
    fontWeight: '400',
  },
  linksRow: {
    flexDirection: 'row',
    gap: 24,
  },
  linkText: {
    color: '#FFFFFF',
    fontSize: 18,
    fontWeight: 'bold',
  },
  exitButton: {
    marginBottom: 10,
    padding: 10,
  },
  exitButtonText: {
    color: '#FFFFFF',
    fontSize: 18,
    fontWeight: '500',
  },
});