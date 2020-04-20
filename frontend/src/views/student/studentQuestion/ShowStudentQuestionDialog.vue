<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ studentQuestion.title }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-question :student-question="studentQuestion" />
        <!--FIXME WTF-->
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="$emit('dialog')">close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import ShowQuestion from '@/views/student/studentQuestion/ShowStudentQuestion.vue';
import StudentQuestion from '@/models/management/StudentQuestion';

@Component({
  components: {
    'show-question': ShowQuestion
  }
})
export default class ShowStudentQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: StudentQuestion, required: true })
  readonly studentQuestion!: StudentQuestion;
}
</script>
