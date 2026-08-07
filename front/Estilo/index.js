import { StyleSheet } from "react-native";

const Styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#B3D8E6', // Cor de fundo azul claro
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 40,
    paddingHorizontal: 24,
  },
  logoCard: {
    width: '90%',
    height: '38%',
    backgroundColor: '#244E70', // Card azul escuro
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
  // Botão com tamanho fixo e borda arredondada do seu CSS original
  primaryButton: {
   width: '90%',
    backgroundColor: '#244E70', // Azul médio dos botões
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

export default Styles;
