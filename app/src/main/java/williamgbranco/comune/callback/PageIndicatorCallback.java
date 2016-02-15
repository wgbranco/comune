package williamgbranco.comune.callback;

/**
 * Created by William Gomes de Branco on 17/09/2015.
 */
public interface PageIndicatorCallback
{
    void moveToIntroPage();

    void moveToConclusionPage();

    void moveToPage(int index);

    void pageChanged(int index);
}
