import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'manualTime',
  standalone: true
})
export class ManualTimePipe implements PipeTransform {
  public static MINUTES_SEPARATOR = ':';
  public static SECONDS_SEPARATOR = '.';
  private static SEARCH_ALL_NON_NUMERIC = /\D/g;

  transform(value: string | undefined): string {
    if (!value) return '';
    if (value.toUpperCase() == 'NT') return 'NT';
    let [minutes, rest = ""] = value.split(ManualTimePipe.MINUTES_SEPARATOR);
    let [seconds = "", centiseconds = ""] = rest.split(ManualTimePipe.SECONDS_SEPARATOR);
    minutes = minutes.replace(ManualTimePipe.SEARCH_ALL_NON_NUMERIC, '');
    seconds = seconds.replace(ManualTimePipe.SEARCH_ALL_NON_NUMERIC, '');
    centiseconds = centiseconds.replace(ManualTimePipe.SEARCH_ALL_NON_NUMERIC, '');
    if (minutes.length > 2) {
      seconds = minutes.slice(2) + seconds;
      minutes = minutes.slice(0, 2);
    }
    if (seconds.length > 2) {
      centiseconds = seconds.slice(2) + centiseconds;
      seconds = seconds.slice(0, 2);
    }
    if (seconds.length > 0) {
      minutes += ManualTimePipe.MINUTES_SEPARATOR;
    }
    if (centiseconds.length > 3) {
      centiseconds = centiseconds.slice(0, 3);
    }
    if (centiseconds.length > 0) {
      seconds += ManualTimePipe.SECONDS_SEPARATOR;
    }
    return minutes + seconds + centiseconds;
  }

}
