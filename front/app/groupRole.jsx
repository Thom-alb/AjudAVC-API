import { View, Text, Pressable } from "react-native";
import { useRouter } from "expo-router";
import Estilos from "../Estilo/groupRole";

export default function GroupRole() {

    const router = useRouter();

    const escolherAnfitriao = () => {
        // Aqui depois vamos chamar a API
        // para definir o usuário como ANFITRIAO

        router.push("/home");
    };

    const escolherAjudante = () => {
        // Aqui depois vamos chamar a API
        // para definir o usuário como AJUDANTE

        router.push("/home");
    };

    const sair = () => {
        router.back();
    };

    return (
        <View style={Estilos.container}>

            <Text style={Estilos.titulo}>
                Você:
            </Text>

            {/* ANFITRIÃO */}
            <Pressable
                style={Estilos.card}
                onPress={escolherAnfitriao}
            >
                <Text style={Estilos.cardTitulo}>
                    Anfitrião
                </Text>

                <Text style={Estilos.cardSubtitulo}>
                    Líder de Grupo
                </Text>

                <View style={Estilos.usuarioIcone}>
                    <View style={Estilos.cabeca} />
                    <View style={Estilos.corpo} />
                </View>
            </Pressable>


            {/* OU */}
            <Text style={Estilos.ou}>
                Ou
            </Text>


            {/* AJUDANTE */}
            <Pressable
                style={Estilos.card}
                onPress={escolherAjudante}
            >
                <Text style={Estilos.cardTitulo}>
                    Ajudante
                </Text>

                <Text style={Estilos.cardSubtitulo}>
                    Membro de Grupo
                </Text>

                <View style={Estilos.grupoIcone}>

                    <View style={Estilos.pessoaFundoEsquerda}>
                        <View style={Estilos.cabecaPequena} />
                        <View style={Estilos.corpoPequeno} />
                    </View>

                    <View style={Estilos.pessoaPrincipal}>
                        <View style={Estilos.cabeca} />
                        <View style={Estilos.corpo} />
                    </View>

                    <View style={Estilos.pessoaFundoDireita}>
                        <View style={Estilos.cabecaPequena} />
                        <View style={Estilos.corpoPequeno} />
                    </View>

                </View>
            </Pressable>


            {/* SAIR */}
            <Pressable
                onPress={sair}
                style={Estilos.botaoSair}
            >
                <Text style={Estilos.textoSair}>
                    Sair
                </Text>
            </Pressable>

        </View>
    );
}