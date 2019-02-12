<template>
    <div class="edityaml">

        <el-row>
            <el-col :span="12">
                <codemirror
                        :options="{
                theme: 'darcula',
                mode: 'yaml',
                lineNumbers: true,
                lineWrapping: true,
                }"
                        :value="yamlText2"
                        v-model="yamlText2"
                >
                </codemirror>
            </el-col>

            <el-col :span="12">
                <div class="grid-content bg-purple-light"></div>
            </el-col>
        </el-row>

        <text-reader @load="plainText = $event"></text-reader>
        <ul>
            <li v-for="data in datas">
                <el-input placeholder="Please input" v-model="data.val">
                    <template slot="prepend">{{data.key_name}}</template>
                </el-input>
            </li>
        </ul>

    </div>
</template>


<style>
    .CodeMirror-scroll {
        text-align: left;
    }

    .el-input__inner {
        width: 200px;
    }
</style>

<script>
    // @ is an alias to /src
    import TextReader from "@/components/yaml.vue";
    import yaml from 'js-yaml'
    import json2yaml from 'json2yaml'
    import 'vue-codemirror'
    import 'codemirror/mode/yaml/yaml.js'
    import 'codemirror/theme/darcula.css'


    import VueCodemirror from 'vue-codemirror'
    // import VueCodeMirror from 'vue-codemirror-lite'

    export default {

        name: 'editYAML',
        data() {
            return {
                plainText: "",
                yamlText: "",
                yamlText2: "",
                JsonData: "",
                datas: [],
                regex: ":",
                datasTmp : []
                // json -> watch -> 다른놈

            }

        },
        mounted() {
            var me = this;
            // $(this.$el).find('.CodeMirror').height(600);
        },
        computed: {},
        components: {
            TextReader,
            yaml,
            json2yaml,
        },
        methods: {
            jsonToDatas(obj, name) {
                var me = this;
                var type = typeof obj
                if (type != "object") {
                    me.datas.push(me.newData(name, obj))
                } else if (typeof obj == "object") {
                    var key_vals = Object.keys(obj)
                    for (var i in key_vals) {
                        var name_val
                        if (name == '') {
                            name_val = key_vals[i] + me.regex
                        } else {
                            name_val = name + key_vals[i] + me.regex
                        }
                        me.jsonToDatas(obj[key_vals[i]], name_val)
                    }

                } else {
                    console.log("Unidentified obj: " + obj)
                }
            },
            newData(key_name, val) {
                var me = this
                let data = {
                    key_list: key_name.split(me.regex),
                    key_name: key_name.replace(/:[0-9]+:/g, ':-:'),
                    val: val
                }
                return data
            },

            isArrayEqual:  function(x, y) {
                return _(x).xorWith(y, _.isEqual).isEmpty();
            }
        },
        watch: {
            plainText: function (newVal) {
                var me = this;
                me.JsonData = yaml.load(newVal)
            },
            yamlText2: function (newVal) {
                // console.log('yamlText2')
                var me = this

                me.JsonData = yaml.load(newVal)
            },
            datas: {
                handler: function (newVal) {
                    var me = this
                    // console.log(newVal[0]['val'])
                    // console.log(me.datasTmp[0]['val'])
                    // return new Promise(function (resolve, reject) {
                    //     if (!me.isArrayEqual(newVal, me.datasTmp)) {
                    //         console.log('datas')
                    //         var jsonTmp = JSON.stringify(newVal);
                    //         me.JsonData = jsonTmp
                    //         console.log('infi')
                    //     }
                    //     resolve(newVal)
                    // }).then(function (newVal) {
                    //     if(!me.isArrayEqual(newVal, me.datasTmp)) {
                    //         me.datasTmp = newVal
                    //     }
                    // })
                },
                deep : true

            },
            JsonData: function (newVal) {
                // console.log('json')
                var me = this
                me.yamlText2 = ''
                me.yamlText2 = json2yaml.stringify(newVal);
                me.datas = []
                // me.datasTmp = []
                me.jsonToDatas(me.JsonData, '')
                if(me.datasTmp.length < 1) {
                    me.datasTmp = me.datas
                }
            }
        }
    }

</script>