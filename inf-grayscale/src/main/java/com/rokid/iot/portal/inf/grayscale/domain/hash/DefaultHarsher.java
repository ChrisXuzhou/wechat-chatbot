package com.rokid.iot.portal.inf.grayscale.domain.hash;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class DefaultHarsher implements Harsher {

    @Override
    public int hash(String origin) {

        Preconditions.checkArgument(
                StringUtils.isNotBlank(origin),
                "to hashed must not be null");
        int hashed = Hashing.murmur3_128().hashString(origin, Charsets.UTF_8).asInt();
        return toLessThan100(hashed);
    }

    private int toLessThan100(int toProc) {
        toProc = toProc < 0 ? -toProc : toProc;
        return toProc % 100;
    }

    public static void main(String[] args) {

        /*
        DefaultHarsher defaultHarsher = new DefaultHarsher();
        String[] strings = {"df13e72a97fe429c970f80a74c5d8986", "9a761219e10b429ab72ffd270789c2ac", "245a2b997f7241c3a33687ba006d3f79", "2f200656a34c4f6e88eea5a2239b71e7", "8bbde23c7eaf48b8b393cb2b985f3bec", "bd2a7ac347174948b9f5945bbab7dd10", "1bb72d8b09934e8491d78132a6dce8ec", "e6f7d39a281a45d6ba1e65e8ba707d4d", "325c45121e924cc896eebc0670dae229", "803c5131561743cc9aca3d6bf017ca18", "e542acea498b4112b5d50f4d079a78ce", "6c93c16387d4426c98309d066d74a3de", "db4fdd4afb7f47a6ba859f8f3bffbbec", "77e1bc0ab0994a81b7fb87d2b90a04f7", "71e8b345a80b4d97af0a81e3108c1fba", "f43904924dd748178a6084d48194db12", "ab18e73be97e4173aa31113c351f31f3", "9bbb6fd71d9b4a39901a6f4db75050b9", "69b3a186d8194662aff3117324b21f97", "13bf37fcbd404d2f875951ea1bb9fb94", "7a2260c6124549d48a5e826cf1d0d837", "5b69984555e14612b9a5985a2cc8ac9f", "a1bac051670b47a9ae75a668bb164aaa", "37f3ef28f0074a2ab4aeedd5c6a4df03", "95978d0a8ce14aebac40545e7679f5aa", "83d208092cb14425884033cdee215748", "8118305e60e24807a1c162d016014e33", "16f623f9478b4c0d9b8b1b5765be4829", "bcfb0d8a456b413fa3c26cfcdf5d602e", "c81311c7e8a846ff8685cbc8568dc639", "b595a45174e44fa6b82a00e47bd89ff0", "8acaf3a380f740bb93820db1e93f57f1", "7332820e79e04d48a56afea2f4721d0f", "feeb3b3949da48348e4a40fdaaa242f4", "ff4963479c7e4290a4457d709c733779", "6d887d776c3142648ce7e337991416cb", "aed06a1af60b47fea078037026db2455", "4c5fc695ea6a41a994d14e9fa3089c88", "6b0eab2c125149a692693a94012a49a2", "cb1d85373b274466b4cb0a26aa5a8bce", "491c929b0ede4819aa1d0e319b7281e0", "6ec31646c3b74b70b47ee86c062d8ff3", "ec7503b4b6694cc0b4eb0ac51232cefe", "6cc964db1d2f49cab8344117882a8267", "971fc75699b340b98fa4e5ff652d011e", "c5caa25975c9470586c48264da369b09", "56a9e5335005440795b93304ec4cc3ee", "f76075fb8fac4a6eb13f1bbc12b26eab", "daa860c28ed64a0b8e243f9ee5da5b31", "19bf63911147436c8368e84f7dffe12c", "3bb5df20c1694d15869a5d79f863f63d", "60f0c7c6ba1a44b48a7e00ea3216da9d", "022ec6bc3ac8461395da01b3f5bd38b9", "31499febdc2246959bc3d5d39ec09cfc", "d3bd8981f71d493cba6d311d05fc6a52", "8040c7952a974af39f5f8795a86308d2", "3fe36e6c12b843c0a216ee3f39b17a3d", "db6b384b64e142abb9cb6aceb0715cd2", "37d1a772e3ed4c4298be4a29046054af", "a344fcc1300949bbbabb4d2c10313329", "ef224a1386ba49ff80ca08bf5e5cb902", "a3db813099b7486e84a0ce4fa6eca007", "1a1474967b4d449d9727cf41fb538e69", "37196d9f77934abc927f0fda5931108f", "7264c9cd035f422886a57dc9519926ef", "2392e937a4e847dcbf2ee8d1de9b1591", "5c3bd3e3d2d54a638cb5b8dc93e20274", "d3ba57eeb1e54201aa7bdbf81b939cf9", "e32992acbf724295935c64b1659b1a9d", "7697c3632ff7464295a608be993a5139", "40c6a1f0e3a04a84bd67670216c81458", "2837bdba7ac54002b46042146bcb5207", "7e9cff84b58a4e718054db36ad503e9e", "eab4cf585b9a47458df58b7eea2eed64", "cceb64dc65904cf89cd28e4a049acc5b", "4d02b7f8dbe94eaa9199511ca6b8feb5", "d277f752f08949ec9bc4c98e4741b542", "162f6e81ae1e4b63bb82a26f715d9e45", "8ca85361e6ab4d6d84eebe3941f25032", "d0b9d5d6979b49e8a92b8d8568a260b4", "49590fc55b3c4df5a95de2de11bfed47", "9df55ed85b134ba8961d56bd83ba4d77", "5f2dd512179146df97fff51a9df66047", "b2b6e22ed9d04ce5a870b9b747fba47c", "276d39d7c9ce49c88221acc0464b1c86", "eb999f3bea40414183e25333cfbfb6d3", "3d95d0913ed946f0874f4ba89b760a7a", "5d794df9baac42e6b616f54b70ec1790", "8e2620bb034045aa866c15957f2bc4f7", "54bfa5c302614ae88d53c6904e604813", "e1bf3c1255d8407c8665ae9491f2f49c", "d36ca27ae3e741679e397b44cbaff00f", "d14e227578eb45828df01e2d217a8815", "83e3677ce9bd4607b77818404296dce7", "577d3f08f733440e9d3d2bc3a8dfdcf9", "f413d5c1d53b4e6b8587868edc69d3f4", "15a7b712cd6f4ce0aaa8a83ce9802e36", "cf8898663fd54c328cbc3545065ce705", "a43eec12cf3a4533878853a39f70a56c", "72efbe206dac45858a5e1cfdc05629c2", "dae47a5aea194275bd7d5fcdc6cdf1ae", "58ed8c3d4171482a82080521febef53e", "54a81fd4ea5c45a4a0226f3bfbc36882", "5dc5efc57d5d43b5aee19922960839b3", "113d48f44c304620ab01b0a76c1e5169", "61bd48cd1a664386a22923be17636576", "c3c2566d55ac4a82a5340d2ed9cb7d50", "8a9c13ae60b14e1eb38f60ef9e4a389e", "f5b183449fa04b68a295f9e942885665", "f9fa55ad88bd439ebad8ffa1180baed1", "0a1f506f84ed402786ab6e413ef0d8c8", "8d00795617004cb3a0c11ae98ee4c395", "9e3393e6de7e4a56978cfff303b86dc3", "6abea88018bc49aa8bcdf6c63bbd65e6", "f08210e40f2b442e8644cbf90f9cad09", "ace6105da16b464694ff7ad33fe6fc36", "7e7076a69c23490db1d44b10b3575f0e", "f674e3b6c4e94ef7ad97ea862275f269", "2c68cec7739a404dadacc72eb48154df", "47f36ff30e3a479a98a499163feee98e", "d1d6963ee7d2489ba2256783433a5892", "9a0fe7238875471cbdf6a900e58f4b5c", "b7393db951d346c693e0d1a3e2a11743", "68ea7b782425435c91aefcd6ff9b7bc6", "691a107fe68b47949df113b049d5b139", "0d2d96bc84fc4b36a1050d4361932f44", "4eb02066bbc2407b831a1b831ddb7b77", "09537022ab2b4d4cbcb4a2a57220428d", "3482122edde24d59904774c1f76d787c", "80c5345b3574467b9c1695b7daf84387", "8662e930eb9a45da8679c6f8098dd334", "41c00dabe30f465488ba1c373a43ae3f", "4aa38f80d2b5470489e5fcd3cd07470c", "0d6356892fb0460a8de440c7211adddb", "1a69d2f1aff2413e9efa7a5ac85b415c", "3fa345f1959e49c8b976911090c27463", "86accd1a2bed45549f6eacbff8813b1d", "4e38f00796014e1e86c0fad6953ba98a", "411511799a7843fba52ca3f550e4b838", "18f8a04ab2ae43ee83474243b57c5812", "4af9e4a320404a2fbf4558ada447744c", "3bc923683c9e4f469f65aab47cd34e7e", "b943a416dd4e4153ade154b67b24065e", "e6e5b313d823495db2cbe97b3430582e", "0bb9dcfc7e574d9192396be94e8f447f", "257e44a6e1cb40c18c8b850346dd7378", "fcb5037081dd440aa88a93b9b1746833", "2f9ce126003542fd9e9e6972e455afd3", "0eda65d1bc1c40ecb033807a9cb6752b", "f6185cd16a004289abfcc4647424d74b", "2fdbe3ec9455446db37b3d18ac49ff3a", "65d7794b18b84b13a3a43eeb751bcd3d", "bb9af018088943dfa9098410c6c0cbaa", "641ffa2f91354cba920f6188b5eef328", "d9e98573d3e149f78cc389fd95399b38", "3a44275bf74845a79f02656f4213f1ef", "14258e07a47a46c6a393708e903ef1f2", "cc11dbde44ea41fd8dc494fe2cdc2632", "9e901c996e0f4805b9747ee7cbef9588", "fdb58f9a5f974d6f9ea6a5c6a0b41189", "db8ce8f35c7a4a59ae7e4ab8fc42c744", "dc0416d890254d1bba6768350b3bb9a4", "7ef44f5f397c463eba524531a3fed927", "21d2e91c8f164a6d812a50e7f28dc939", "124ee13288804d29b892d39b26e76c79", "9580dfeeddd548fab15f030bbb159bb8", "4c4d7a86c8e54dc188e3d92772c58652", "0bfb945646d54d7bab8d7533ba0ad962", "04b61f161aeb4683a4a79aec8cbcb4de", "ac18ea12529944c8a49d1e2696b687cf", "0a7c5a22064c4bfc8c9fd3d464895b61", "34c5eb66d2b94ace8d657d709716916c", "23f06081a0eb45c4bd937f2274c1d80d", "774f8cb9fe44488b8a46c9579a82b67f", "f1b218ea59a84bcbaaaa242518038923", "de2955999bde4567b2f799cf5d46cbff", "1a161f013363445ca87b2306d262a679", "a9cd070ad638496d86cb5e2a987fa799", "c17c1d45fa654d6089e2e65147741900", "ade7234bcd664f9ebdf7f8c11db311f7", "d004a375eb5f4427927f0404d13b2265", "b75d41a731bb4f9aa6d6951ff2ae5d62", "c22a408d891d4302aeb8118a97acbdfb", "41ff9c6da90f41f6ae7d8c557b52139e", "6e834feff7244a1a9cca712870d3411c", "81bc9221ac69404781012cfbb33ef3a0", "33ccfdceaa2e410880522c02ebf302fb", "b55c560b4dee47fe839b78c76e0017e8", "ce5ee02cd3d74e869652e2c1ce3500e5", "8150bcd368d744f9a35b3b134a9e49f4", "2c22fd3b24764ce6a73f121d5744c623", "a13f55aa8fd348788c56af21beba15c4", "7dc8592c40c445619887d049a9b5e171", "8aaa8599ed75435b8049d65f6c28fdf6", "83f9d35c63ff4fcfa9d3e5c8167937d4", "544f69a4d8f544e0b01ca3bbc8bdd2e1", "607f0587c6d840adaa0e7ba4efb4de4b", "8bed97249d3d484a8ebf80f8a412d68a", "c60e30be37fd4b46bf0b9e499529fe47", "b47122d5ed4f4b2d9a1c21ef902093c5"};
        int conflict = 0;
        Set<Integer> compared = Sets.newHashSet();

        int cnt = 0;
        for (String string : strings) {
            cnt++;
            int hashed = defaultHarsher.hash(string);
            System.out.println(hashed);

            if (hashed < 10) {
                conflict++;
            }
            compared.add(hashed);
        }
        double percent = (conflict / ((double) cnt)) * 100;

        System.out.println("===============\n\n\n" + conflict + "," + percent + "%");

        */





        DefaultHarsher defaultHarsher = new DefaultHarsher();

        Set<Integer> compared = Sets.newHashSet();

        int conflict = 0;

        int cnt = 20000;
        for (int i = 0; i < cnt; i++) {
            String raw = UUID.randomUUID().toString().replace("-", "");
            int hashed = defaultHarsher.hash(raw);
            System.out.println(hashed);

            if (hashed < 60) {
                conflict++;
            }
            compared.add(hashed);
        }

        double percent = (conflict / ((double) cnt)) * 100;

        System.out.println("===============\n\n\n" + conflict + "," + percent + "%");





    }
}
