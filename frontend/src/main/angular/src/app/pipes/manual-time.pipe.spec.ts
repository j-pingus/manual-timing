import { ManualTimePipe } from './manual-time.pipe';

describe('ManualTimePipe', () => {
  it('create an instance', () => {
    const pipe = new ManualTimePipe();
    expect(pipe).toBeTruthy();
  });
  it( 'transform input',()=> {
    const ms = ManualTimePipe.MINUTES_SEPARATOR;
    const ss = ManualTimePipe.SECONDS_SEPARATOR;
    const pipe = new ManualTimePipe();
    expect(pipe.transform('0')).toEqual('0');
    expect(pipe.transform('00')).toEqual('00');
    expect(pipe.transform('00' + ms)).toEqual('00');
    expect(pipe.transform('000')).toEqual('00' + ms + '0');
    expect(pipe.transform('00' + ms + '0')).toEqual('00' + ms + '0');
  });
});
