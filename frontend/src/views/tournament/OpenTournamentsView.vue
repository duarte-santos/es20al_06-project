<template>
  <div class="container">
    <h2>Open Tournaments</h2>
    <ul>
      <li class="list-header">
        <div class="col">Title</div>
        <div class="col">Tournament</div>
        <div class="col">Tournaments</div>
        <div class="col last-col"></div>
      </li>
      <li
              class="list-row"
              v-for="quiz in quizzes"
              :key="quiz.quizAnswerId"
              @click="solveQuiz(quiz)"
      >
        <div class="col">
          {{ quiz.title }}
        </div>
        <div class="col">
          {{ quiz.availableDate }}
        </div>
        <div class="col">
          {{ quiz.conclusionDate }}
        </div>
        <div class="col last-col">
          <i class="fas fa-chevron-circle-right"></i>
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Course from '@/models/user/Course';
import RemoteServices from '@/services/RemoteServices';

interface CourseMap {
  [key: string]: Course[];
}

@Component
export default class CourseSelectionView extends Vue {
  courseExecutions: CourseMap | null = null;
  confirmationDialog: Boolean = false;
  selectedCourse: Course | null = null;

  async created() {
    this.courseExecutions = await this.$store.getters.getUser.courses;
  }

  async selectCourse(course: Course) {
    if (course.status !== 'INACTIVE') {
      await this.$store.dispatch('currentCourse', course);
      await this.$router.push({ name: 'home' });
    } else {
      this.selectedCourse = course;
      this.confirmationDialog = true;
    }
  }

  async activateCourse() {
    this.confirmationDialog = false;
    try {
      if (this.selectedCourse) {
        this.selectedCourse = await RemoteServices.activateCourse(
          this.selectedCourse
        );
        await this.$store.dispatch('currentCourse', this.selectedCourse);
        await this.$router.push({ name: 'home' });
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  unselectCourse() {
    this.selectedCourse = null;
    this.confirmationDialog = false;
  }
}
</script>

<style lang="scss">
.title {
  text-align: center;
  font-family: 'Baloo Tamma', cursive;
}

.bold {
  font-weight: bolder;
  text-decoration: underline;
}

.active {
  background-color: #42b983;
  .v-icon {
    padding: 0;
  }
}

.inactive {
  background-color: #7f7f7f;
  .v-icon {
    padding: 0;
  }
}

.historic {
  background-color: cornflowerblue;
  .v-icon {
    padding: 0;
  }
}
</style>
