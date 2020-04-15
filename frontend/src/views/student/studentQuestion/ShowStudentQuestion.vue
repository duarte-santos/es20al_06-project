<template>
  <div>
    <span
      v-html="convertMarkDown(studentQuestion.content, studentQuestion.image)"
    />
    <ul>
      <li v-for="(option, index) in studentQuestion.options" :key="index">
        <span
          v-if="studentQuestion.correct - 1 === index"
          v-html="convertMarkDown('**[★]** ', null)"
        />
        <span
          v-html="convertMarkDown(option, null)"
          v-bind:class="[
            studentQuestion.correct - 1 === index ? 'font-weight-bold' : ''
          ]"
        />
      </li>
    </ul>
    <br />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import StudentQuestion from '@/models/management/StudentQuestion';
import Image from '@/models/management/Image';

@Component
export default class ShowStudentQuestion extends Vue {
  @Prop({ type: StudentQuestion, required: true })
  readonly studentQuestion!: StudentQuestion;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
