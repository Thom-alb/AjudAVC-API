import { StyleSheet } from "react-native";

const Styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#0e1f2c", // Azul do fundo principal
    justifyContent: "center",
    alignItems: "center",
    padding: 20,
  },
  card: {
    width: "100%",
    backgroundColor: "#244E70", // Azul do card
    borderRadius: 24,
    padding: 24,
    alignItems: "stretch",
  },
  backButton: {
    marginBottom: 8,
    alignSelf: "flex-start",
  },
  title: {
    fontSize: 22,
    fontWeight: "600",
    color: "#FFFFFF",
    textAlign: "center",
    marginBottom: 32,
  },
  input: {
    height: 48,
    borderBottomWidth: 1,
    borderBottomColor: "#6C9BCF",
    color: "#FFFFFF",
    fontSize: 16,
    marginBottom: 20,
    paddingHorizontal: 4,
  },
  checkboxContainer: {
    flexDirection: "row",
    alignItems: "center",
    marginVertical: 12,
  },
  checkbox: {
    width: 18,
    height: 18,
    borderWidth: 1,
    borderColor: "#FFFFFF",
    marginRight: 10,
    justifyContent: "center",
    alignItems: "center",
  },
  checkboxChecked: {
    backgroundColor: "#6FA4E8",
    borderColor: "#6FA4E8",
  },
  checkboxLabel: {
    color: "#FFFFFF",
    fontSize: 14,
  },
  buttonPrimary: {
    backgroundColor: "#6FA4E8",
    height: 48,
    borderRadius: 24,
    justifyContent: "center",
    alignItems: "center",
    marginTop: 20,
    marginBottom: 16,
  },
  buttonText: {
    color: "#FFFFFF",
    fontSize: 18,
    fontWeight: "bold",
  },
  forgotContainer: {
    alignItems: "flex-start",
    marginTop: 4,
  },
  forgotText: {
    color: "#E0E0E0",
    fontSize: 13,
  },
  exitButton: {
    marginTop: 32,
  },
  exitButtonText: {
    color: "#FFFFFF",
    fontSize: 16,
  },
});
export default Styles;