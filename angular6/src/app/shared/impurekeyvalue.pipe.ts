import { PipeTransform, Pipe } from '@angular/core';

@Pipe({name: 'impureKeys',  pure: false })
export class ImpureKeysPipe implements PipeTransform {
  transform(value, args:string[]) : any {
    let keys = [];
    for (let key in value) {
      keys.push({key: key, value: value[key]});
    }
    return keys;
  }
}