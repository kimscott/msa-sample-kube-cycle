<template>
    <div class="edityaml">

        <el-row>
            <el-col :span="12">
                <codemirror
                        :options="{
                theme: 'dracula',
                mode: 'yaml',
                extraKeys: {'Ctrl-Space': 'autocomplete'},
                lineNumbers: true,
                lineWrapping: true,
                }"
                        :value="yamlText">
                </codemirror>
            </el-col>

            <el-col :span="12"><div class="grid-content bg-purple-light"></div></el-col>
        </el-row>

        <text-reader @load="plainText = $event"></text-reader>
    </div>
</template>

<script>
    // @ is an alias to /src
    import TextReader from "@/components/yaml.vue";
    import yaml from 'js-yaml'
    import json2yaml from 'json2yaml'
    import 'codemirror/mode/yaml'
    // import VueCodeMirror from 'vue-codemirror-lite'



    export default {

        name: 'editYAML',
        data() {
            return {
                plainText: "",
                yamlText: "",
                yamlText2: "",
            }
        },
        mounted () {
            var me = this;
            // $(this.$el).find('.CodeMirror').height(600);
        },
        components: {
            TextReader,
            yaml,
            json2yaml,
        },
        watch: {
            plainText: function (newVal) {
                var me = this;
                me.yamlText = JSON.stringify(yaml.load(newVal), null, 2)
            },
            yamlText: function (newVal) {
                var me = this
                me.yamlText2 = json2yaml.stringify(JSON.parse(newVal))
            }
        }
    }
</script>
