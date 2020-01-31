import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filter'
})
export class JumbuneFilterPipe implements PipeTransform {

  transform(arr: any, searchTerm: any): any {
    // check if search term is undefined
    if(searchTerm === undefined) return arr;
    return arr.filter(res=>{ // javascript filter(function)
      // if below is false, then topic will be removed from topics array
      return res.name.toLowerCase().includes(searchTerm.toLowerCase());
    });
  }

}