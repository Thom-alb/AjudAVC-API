import { StyleSheet } from "react-native";

const Estilos = StyleSheet.create({

    container: {
        flex: 1,
        backgroundColor: "#75A9D2",
        alignItems: "center",
        paddingTop: 70,
    },

    titulo: {
        fontSize: 42,
        fontWeight: "bold",
        color: "#12456D",
        marginBottom: 60,
    },

    card: {
        width: "72%",
        height: 300,
        backgroundColor: "#155B94",
        borderRadius: 45,
        alignItems: "center",
        paddingTop: 25,
    },

    cardTitulo: {
        fontSize: 34,
        fontWeight: "bold",
        color: "#8CC8FF",
    },

    cardSubtitulo: {
        fontSize: 32,
        fontWeight: "bold",
        color: "#8CC8FF",
        marginTop: 5,
    },

    ou: {
        fontSize: 42,
        fontWeight: "bold",
        color: "#12456D",
        marginVertical: 55,
    },

    usuarioIcone: {
        alignItems: "center",
        marginTop: 25,
    },

    cabeca: {
        width: 125,
        height: 125,
        borderRadius: 100,
        backgroundColor: "#2196E0",
    },

    corpo: {
        width: 220,
        height: 105,
        backgroundColor: "#2196E0",
        borderTopLeftRadius: 80,
        borderTopRightRadius: 80,
        marginTop: 15,
    },

    grupoIcone: {
        width: 280,
        height: 150,
        position: "relative",
        marginTop: 25,
        alignItems: "center",
    },

    pessoaPrincipal: {
        position: "absolute",
        zIndex: 3,
        alignItems: "center",
    },

    pessoaFundoEsquerda: {
        position: "absolute",
        left: 0,
        top: 0,
        alignItems: "center",
        opacity: 0.6,
    },

    pessoaFundoDireita: {
        position: "absolute",
        right: 0,
        top: 0,
        alignItems: "center",
        opacity: 0.6,
    },

    cabecaPequena: {
        width: 75,
        height: 75,
        borderRadius: 50,
        backgroundColor: "#A7C9E5",
    },

    corpoPequeno: {
        width: 120,
        height: 65,
        backgroundColor: "#A7C9E5",
        borderTopLeftRadius: 50,
        borderTopRightRadius: 50,
        marginTop: 5,
    },

    botaoSair: {
        marginTop: 45,
        padding: 10,
    },

    textoSair: {
        color: "#FFFFFF",
        fontSize: 28,
    },

});

export default Estilos;