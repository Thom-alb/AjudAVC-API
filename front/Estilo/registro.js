import { StyleSheet } from "react-native";

const Styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0e1f2c',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  card: {
    width: '100%',
    backgroundColor: '#244E70',
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
    alignSelf: 'center',
  },
  exitButtonText: {
    color: '#FFFFFF',
    fontSize: 16,
  },

  scrollContent: {
    flexGrow: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: 20,
    width: '100%',
  },

  passwordContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    borderBottomWidth: 1,
    borderBottomColor: '#6C9BCF',
    marginBottom: 14,
  },
  passwordInput: {
    flex: 1,
    height: 44,
    color: '#FFFFFF',
    fontSize: 15,
    paddingHorizontal: 4,
  },
  eyeIcon: {
    paddingHorizontal: 8,
    justifyContent: 'center',
  },

  dividerContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: 12,
  },
  dividerLine: {
    flex: 1,
    height: 1,
    backgroundColor: '#6C9BCF',
  },
  dividerText: {
    color: '#E0E0E0',
    paddingHorizontal: 10,
    fontSize: 12,
    fontWeight: '600',
  },

  googleButton: {
    flexDirection: 'row',
    backgroundColor: '#FFFFFF',
    height: 48,
    borderRadius: 24,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 16,
  },
  googleButtonText: {
    color: '#0e1f2c',
    fontSize: 15,
    fontWeight: '600',
  },
});

export default Styles;