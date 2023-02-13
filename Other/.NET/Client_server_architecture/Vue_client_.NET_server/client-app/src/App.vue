<template>
  <post-request/>
</template>

<script>
import PostRequest from './components/PostRequest.vue'

export default {
  name: 'App',
  components: {
    PostRequest
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: left;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
