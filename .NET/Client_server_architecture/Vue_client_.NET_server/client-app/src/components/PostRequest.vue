<template>
  <div>
    <input style="margin:10px" v-model="message"/>  
    <button @click="sendSearchRequest">Search!</button>
    <pre>{{JSON.stringify(searchResults, null, 2)}}</pre>
  </div>
</template>

<script>
export default {
  name: "search-request",
  data() {
    return {
      message: null,
      searchResults: null
    };
  },
  methods: {
    sendSearchRequest() {
      const requestOptions = {
        method: "GET",
      };
      fetch("http://localhost:5243/search/" + this.message, requestOptions)
        .then(response => response.json())
        .then(data => (this.searchResults = data))
    }
  }
};
</script>